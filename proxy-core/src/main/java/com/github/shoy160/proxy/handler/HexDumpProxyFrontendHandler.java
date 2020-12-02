package com.github.shoy160.proxy.handler;

import com.github.shoy160.proxy.adapter.ChannelAdapter;
import com.github.shoy160.proxy.util.SpringUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author shay
 * @date 2020/12/2
 */
@Slf4j
public class HexDumpProxyFrontendHandler extends ChannelInboundHandlerAdapter {

    private final String remoteHost;
    private final int remotePort;
    private Channel outboundChannel;
    private final static String ENTER_HEX = "0d0a";

    public HexDumpProxyFrontendHandler(String remoteHost, int remotePort) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    private void createOutChannel(final Channel inboundChannel) {
        Bootstrap b = new Bootstrap();
        log.info("channel :{}", inboundChannel.getClass().getName());
        b.group(inboundChannel.eventLoop())
                .channel(inboundChannel.getClass())
                .option(ChannelOption.AUTO_READ, false)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) {
                        ChannelAdapter adapter = SpringUtils.getObject(ChannelAdapter.class);
                        if (adapter != null) {
                            adapter.onBackendPipeline(channel.pipeline());
                        }
                        channel.pipeline()
//                                .addLast(new LimiterHandler())
                                .addLast(new HexDumpProxyBackendHandler(inboundChannel));

                    }
                });
        ChannelFuture f = b.connect(remoteHost, remotePort);
        outboundChannel = f.channel();
        f.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                inboundChannel.read();
            } else {
                inboundChannel.close();
            }
        });
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        final Channel inboundChannel = ctx.channel();
        createOutChannel(inboundChannel);
    }


    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        if (outboundChannel.isActive()) {
            if (msg instanceof ByteBuf) {
                ChannelAdapter adapter = SpringUtils.getObject(ChannelAdapter.class);
                if (adapter != null) {
                    adapter.onFrontend((ByteBuf) msg, outboundChannel);
                }
            }
            outboundChannel
                    .writeAndFlush(msg)
                    .addListener((ChannelFutureListener) future -> {
                        if (future.isSuccess()) {
                            ctx.channel().read();
                        } else {
                            future.channel().close();
                        }
                    });
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (outboundChannel != null) {
            closeOnFlush(outboundChannel);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        closeOnFlush(ctx.channel());
    }

    /**
     * Closes the specified channel after all queued write requests are flushed.
     */
    static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}