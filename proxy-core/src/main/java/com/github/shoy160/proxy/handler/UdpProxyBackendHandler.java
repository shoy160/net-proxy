package com.github.shoy160.proxy.handler;

import com.github.shoy160.proxy.Constants;
import com.github.shoy160.proxy.adapter.ChannelAdapter;
import com.github.shoy160.proxy.util.BufferUtils;
import com.github.shoy160.proxy.util.SpringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
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

    private final ChannelHandlerContext inboundContext;
    private final InetSocketAddress senderAddress;

    public UdpProxyBackendHandler(ChannelHandlerContext inboundContext, InetSocketAddress senderAddress) {
        this.inboundContext = inboundContext;
        this.senderAddress = senderAddress;
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof DatagramPacket) {
            DatagramPacket packet = (DatagramPacket) msg;
            ByteBuf content = packet.content();
            if (inboundContext.channel().hasAttr(Constants.ATTR_ADAPTER)) {
                ChannelAdapter adapter = inboundContext.channel().attr(Constants.ATTR_ADAPTER).get();
                content = adapter.onBackend(content, ctx.channel());
            }
            inboundContext
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
