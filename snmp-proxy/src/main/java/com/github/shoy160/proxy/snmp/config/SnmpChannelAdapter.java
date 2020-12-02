package com.github.shoy160.proxy.snmp.config;

import com.github.shoy160.proxy.adapter.ChannelAdapter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;

/**
 * @author shay
 * @date 2020/12/2
 */
public class SnmpChannelAdapter implements ChannelAdapter {
    @Override
    public void onFrontend(ByteBuf buf, Channel channel) {

    }

    @Override
    public void onBackendPipeline(ChannelPipeline pipeline) {

    }

    @Override
    public void onFrontendPipeline(ChannelPipeline pipeline) {

    }

    @Override
    public void onBackend(ByteBuf buf, Channel channel) {

    }
}
