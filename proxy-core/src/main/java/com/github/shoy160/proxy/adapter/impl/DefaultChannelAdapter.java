package com.github.shoy160.proxy.adapter.impl;

import com.github.shoy160.proxy.adapter.ChannelAdapter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;

/**
 * @author shay
 * @date 2020/12/2
 */
public class DefaultChannelAdapter implements ChannelAdapter {
    @Override
    public ByteBuf onFrontend(ByteBuf buf, Channel channel) {
        return buf;
    }

    @Override
    public void onBackendPipeline(ChannelPipeline pipeline) {

    }

    @Override
    public void onFrontendPipeline(ChannelPipeline pipeline) {

    }

    @Override
    public ByteBuf onBackend(ByteBuf buf, Channel channel) {
        return buf;
    }
}
