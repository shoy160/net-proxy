package com.github.shoy160.proxy.handler;

import com.github.shoy160.proxy.Constants;
import com.github.shoy160.proxy.adapter.ChannelAdapter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author shay
 */
@Slf4j
public class UdpProxyBackendHandler extends ChannelInboundHandlerAdapter {

    private final ChannelHandlerContext frontChannelContext;
    private final InetSocketAddress senderAddress;

    public UdpProxyBackendHandler(ChannelHandlerContext inboundContext, InetSocketAddress senderAddress) {
        this.frontChannelContext = inboundContext;
        this.senderAddress = senderAddress;
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof DatagramPacket) {
            DatagramPacket packet = (DatagramPacket) msg;
            ByteBuf content = packet.content();
            Channel frontChannel = frontChannelContext.channel();
            if (frontChannel.hasAttr(Constants.ATTR_ADAPTER)) {
                ChannelAdapter adapter = frontChannel.attr(Constants.ATTR_ADAPTER).get();
                content = adapter.onBackend(content, frontChannel, ctx.channel());
            }
            frontChannelContext
                    .writeAndFlush(new DatagramPacket(content, this.senderAddress));
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.info("channel unregistered:{}", ctx.channel().localAddress());
        super.channelUnregistered(ctx);
    }
}
