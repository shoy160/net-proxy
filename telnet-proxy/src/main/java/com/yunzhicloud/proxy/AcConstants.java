package com.yunzhicloud.proxy;

import io.netty.util.AttributeKey;

/**
 * @author shay
 * @date 2020/12/12
 */
public interface AcConstants {
    AttributeKey<Boolean> ATTR_STA_TAG = AttributeKey.valueOf("sta_tag");
}
