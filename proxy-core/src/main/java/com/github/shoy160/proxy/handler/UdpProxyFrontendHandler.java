package com.github.shoy160.proxy.handler;

import com.github.shoy160.proxy.adapter.ChannelAdapter;
import com.github.shoy160.proxy.util.BufferUtils;
import com.github.shoy160.proxy.util.SpringUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author shay
 * @date 2020/12/2
 */
@Slf4j
public class UdpProxyFrontendHandler extends ChannelInboundHandlerAdapter {

    private final String remoteHost;
    private final int remotePort;

    public UdpProxyFrontendHandler(String remoteHost, int remotePort) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    private void sendMessage(final ChannelHandlerContext context, final DatagramPacket packet) {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        InetSocketAddress address = new InetSocketAddress(remoteHost, remotePort);

        bootstrap.group(group)
                .channel(context.channel().getClass())
                .remoteAddress(address)
                .handler(new ChannelInitializer<NioDatagramChannel>() {
                    @Override
                    protected void initChannel(NioDatagramChannel channel) {
                        ChannelPipeline pipeline = channel.pipeline();
                        ChannelAdapter adapter = SpringUtils.getObject(ChannelAdapter.class);
                        if (adapter != null) {
                            adapter.onBackendPipeline(pipeline);
                        }
                        pipeline.addLast(new UdpProxyBackendHandler(context, packet.sender()));
                    }
                });
        try {
            Channel channel = bootstrap.bind(0).sync().channel();
            log.info("send to back end => udp:{}/{}", remoteHost, remotePort);
            channel.writeAndFlush(new DatagramPacket(packet.content(), address));
            channel.closeFuture().await(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof DatagramPacket) {
            DatagramPacket packet = (DatagramPacket) msg;
//            log.info("front read:{},sender:{}", BufferUtils.toHex(packet.content()), packet.sender());
            sendMessage(ctx, packet);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }
}