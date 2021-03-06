package com.yunzhicloud.proxy.config;

import com.github.shoy160.proxy.adapter.impl.TelnetChannelAdapter;
import com.yunzhicloud.proxy.filter.AcFilter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author shay
 * @date 2020/12/12
 */
@Slf4j
@Component
public class AcChannelAdapter extends TelnetChannelAdapter {

    private final AcFilter filter;

    public AcChannelAdapter(AcFilter filter) {
        this.filter = filter;
    }

    @Override
    public ByteBuf onBackend(ByteBuf buf, Channel front, Channel backend) {
        buf = super.onBackend(buf, front, backend);
        return this.filter.transform(buf, backend);
    }
}
