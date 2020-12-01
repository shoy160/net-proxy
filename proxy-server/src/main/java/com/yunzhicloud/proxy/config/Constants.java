package com.yunzhicloud.proxy.config;

import io.netty.util.AttributeKey;

/**
 * @author shay
 * @date 2020/11/30
 */
public interface Constants {
    String KEY_INPUT_PERCENT = "";
    String KEY_OUTPUT_PERCENT = "";
    String KEY_LIMITER = "limiters";
    AttributeKey<String> ATTR_HOST_NAME = AttributeKey.valueOf("host-name");
    /**
     * 开始输出标签
     */
    AttributeKey<Boolean> ATTR_STDOUT = AttributeKey.valueOf("std-out");
}
