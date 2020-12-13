package com.github.shoy160.proxy.handler;

import com.github.shoy160.proxy.Constants;
import com.github.shoy160.proxy.adapter.ChannelAdapter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author shay
 */
@Slf4j
public class TcpProxyBackendHandler extends ChannelInboundHandlerAdapter {

    private final Channel frontChannel;

    public TcpProxyBackendHandler(Channel frontChannel) {
        this.frontChannel = frontChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.read();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        if (frontChannel.hasAttr(Constants.ATTR_ADAPTER)) {
            ChannelAdapter adapter = frontChannel.attr(Constants.ATTR_ADAPTER).get();
            msg = adapter.onBackend((ByteBuf) msg, frontChannel, ctx.channel());
        }
        frontChannel
                .writeAndFlush(msg)
                .addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        ctx.channel().read();
                    } else {
                        future.channel().close();
                    }
                });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        TcpProxyFrontendHandler.closeOnFlush(frontChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        TcpProxyFrontendHandler.closeOnFlush(ctx.channel());
    }
}
