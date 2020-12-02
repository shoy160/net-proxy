package com.yunzhicloud.proxy.config;

import com.github.shoy160.proxy.Constants;
import com.github.shoy160.proxy.adapter.ChannelAdapter;
import com.github.shoy160.proxy.util.BufferUtils;
import com.yunzhicloud.proxy.filter.TransferManager;
import com.yunzhicloud.proxy.handler.LimiterHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author shay
 * @date 2020/12/2
 */
@Slf4j
//@Component
public class TelnetChannelAdapter implements ChannelAdapter {
    private final static String ENTER_HEX = "0d0a";
    private final TransferManager transferManager;

    public TelnetChannelAdapter(TransferManager transferManager) {
        this.transferManager = transferManager;
    }

    @Override
    public Object onFrontend(ByteBuf buf, Channel channel) {
        String hex = BufferUtils.toHex(buf);
        log.info("cmd:{}", hex);
        channel.attr(Constants.ATTR_STDOUT).set(hex.equals(ENTER_HEX));
        return buf;
    }

    @Override
    public void onBackendPipeline(ChannelPipeline pipeline) {
        pipeline.addLast(new LimiterHandler());
    }

    @Override
    public void onFrontendPipeline(ChannelPipeline pipeline) {

    }

    @Override
    public Object onBackend(ByteBuf buf, Channel channel) {
        return transferManager.transferMsg(buf, channel);
    }
}
