package com.yunzhicloud.proxy.config;

import com.github.shoy160.proxy.adapter.impl.TelnetChannelAdapter;
import com.yunzhicloud.proxy.filter.TransferManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author shay
 * @date 2020/12/2
 */
@Slf4j
@Component
public class EthChannelAdapter extends TelnetChannelAdapter {
    private final TransferManager transferManager;

    public EthChannelAdapter(TransferManager transferManager) {
        this.transferManager = transferManager;
    }

    @Override
    public ByteBuf onBackend(ByteBuf buf, Channel front, Channel backend) {
        buf = super.onBackend(buf, front, backend);
        return transferManager.transferMsg(buf);
    }
}
