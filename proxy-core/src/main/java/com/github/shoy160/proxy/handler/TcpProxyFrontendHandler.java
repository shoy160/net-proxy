package com.github.shoy160.proxy.handler;

import com.github.shoy160.proxy.Constants;
import com.github.shoy160.proxy.adapter.ChannelAdapter;
import com.github.shoy160.proxy.config.ProxyListenerConfig;
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
public class TcpProxyFrontendHandler extends ChannelInboundHandlerAdapter {

    private final ProxyListenerConfig config;
    private Channel backendChannel;

    public TcpProxyFrontendHandler(ProxyListenerConfig config) {
        this.config = config;
    }

    private void createOutChannel(final Channel frontChannel) {
        Bootstrap b = new Bootstrap();
        log.info("channel :{}", frontChannel.getClass().getName());
        b.group(frontChannel.eventLoop())
                .channel(frontChannel.getClass())
                .option(ChannelOption.AUTO_READ, false)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) {
                        ChannelPipeline pipeline = channel.pipeline();
                        if (frontChannel.hasAttr(Constants.ATTR_ADAPTER)) {
                            ChannelAdapter adapter = frontChannel.attr(Constants.ATTR_ADAPTER).get();
                            adapter.onBackendPipeline(pipeline);
                        }
                        pipeline.addLast(new TcpProxyBackendHandler(frontChannel));

                    }
                });
        ChannelFuture f = b.connect(this.config.getRemoteIp(), this.config.getRemotePort());
        backendChannel = f.channel();
        f.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                frontChannel.read();
            } else {
                frontChannel.close();
            }
        });
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        final Channel inboundChannel = ctx.channel();
        this.config.saveConfig(inboundChannel);
        createOutChannel(inboundChannel);
    }


    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        if (backendChannel.isActive()) {
            if (msg instanceof ByteBuf && ctx.channel().hasAttr(Constants.ATTR_ADAPTER)) {
                ctx.channel().attr(Constants.ATTR_ADAPTER).get().onFrontend((ByteBuf) msg, ctx.channel(), backendChannel);
            }
            backendChannel
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
        if (backendChannel != null) {
            closeOnFlush(backendChannel);
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