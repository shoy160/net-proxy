package com.yunzhicloud.proxy.config;

import com.github.shoy160.proxy.Constants;
import com.github.shoy160.proxy.adapter.ChannelAdapter;
import com.github.shoy160.proxy.util.BufferUtils;
import com.yunzhicloud.proxy.filter.TransferManager;
import com.yunzhicloud.proxy.handler.LimiterHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author shay
 * @date 2020/12/2
 */
@Slf4j
@Component
public class TelnetChannelAdapter implements ChannelAdapter {
    private final static byte[][] ENTER_HEX = new byte[][]{
            new byte[]{0x0d, 0x0a},
            new byte[]{0x0d, 0x00},
            new byte[]{0x0d}
    };
    private final TransferManager transferManager;

    public TelnetChannelAdapter(TransferManager transferManager) {
        this.transferManager = transferManager;
    }

    @Override
    public ByteBuf onFrontend(ByteBuf buf, Channel channel) {
        byte[] bytes = ByteBufUtil.getBytes(buf);
        boolean enter = Arrays.equals(bytes, ENTER_HEX[0])
                || Arrays.equals(bytes, ENTER_HEX[1])
                || Arrays.equals(bytes, ENTER_HEX[2]);
        channel.attr(Constants.ATTR_STDOUT).set(enter);
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
    public ByteBuf onBackend(ByteBuf buf, Channel channel) {
        return transferManager.transferMsg(buf);
    }
}
