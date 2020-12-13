package com.github.shoy160.proxy.handler;

import com.github.shoy160.proxy.Constants;
import com.github.shoy160.proxy.adapter.ChannelAdapter;
import com.github.shoy160.proxy.config.ProxyListenerConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
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

    private final ProxyListenerConfig config;

    public UdpProxyFrontendHandler(ProxyListenerConfig config) {
        this.config = config;
    }

    private void sendMessage(final ChannelHandlerContext context, final DatagramPacket packet) {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        InetSocketAddress address = new InetSocketAddress(this.config.getRemoteIp(), this.config.getRemotePort());

        bootstrap.group(group)
                .channel(context.channel().getClass())
                .remoteAddress(address)
                .handler(new ChannelInitializer<NioDatagramChannel>() {
                    @Override
                    protected void initChannel(NioDatagramChannel channel) {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new UdpProxyBackendHandler(context, packet.sender()));
                    }
                });
        try {
            Channel channel = bootstrap.bind(0).sync().channel();
            channel.writeAndFlush(new DatagramPacket(packet.content(), address));
            channel.closeFuture().await(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        final Channel channel = ctx.channel();
        this.config.saveConfig(channel);
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof DatagramPacket) {
            DatagramPacket packet = (DatagramPacket) msg;
            if (ctx.channel().hasAttr(Constants.ATTR_ADAPTER)) {
                ChannelAdapter adapter = ctx.channel().attr(Constants.ATTR_ADAPTER).get();
                ByteBuf content = adapter.onFrontend(packet.content(), ctx.channel(), null);
                packet = packet.replace(content);
            }
            sendMessage(ctx, packet);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }
}