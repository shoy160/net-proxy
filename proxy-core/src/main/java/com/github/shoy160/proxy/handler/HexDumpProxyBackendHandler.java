package com.github.shoy160.proxy.handler;

import com.github.shoy160.proxy.adapter.ChannelAdapter;
import com.github.shoy160.proxy.util.SpringUtils;
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
public class HexDumpProxyBackendHandler extends ChannelInboundHandlerAdapter {

    private final Channel inboundChannel;

    public HexDumpProxyBackendHandler(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.read();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        ChannelAdapter adapter = SpringUtils.getObject(ChannelAdapter.class);
        if (adapter != null) {
            msg = adapter.onBackend((ByteBuf) msg, ctx.channel());
        }
        inboundChannel
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
        HexDumpProxyFrontendHandler.closeOnFlush(inboundChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        HexDumpProxyFrontendHandler.closeOnFlush(ctx.channel());
    }
}
