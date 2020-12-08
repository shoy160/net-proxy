package com.github.shoy160.proxy;

import com.github.shoy160.proxy.adapter.ChannelAdapter;
import io.netty.util.AttributeKey;

/**
 * @author shay
 * @date 2020/12/2
 */
public interface Constants {
    String KEY_LIMITER = "limiters";
    AttributeKey<String> ATTR_HOST_NAME = AttributeKey.valueOf("host-name");
    /**
     * 开始输出标签
     */
    AttributeKey<Boolean> ATTR_STDOUT = AttributeKey.valueOf("std-out");

    AttributeKey<ChannelAdapter> ATTR_ADAPTER = AttributeKey.valueOf("channel-adapter");
}
