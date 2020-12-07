package com.github.shoy160.proxy.config;

import com.yunzhicloud.core.enums.ValueNameEnum;

/**
 * @author shay
 * @date 2020/12/3
 */
public enum ProtocolType implements ValueNameEnum<Integer> {
    /**
     * TCP
     */
    TCP(0, "TCP"),
    /**
     * UDP
     */
    UDP(1, "UDP");

    private final int value;
    private final String text;

    ProtocolType(int value, String text) {
        this.value = value;
        this.text = text;
    }

    @Override
    public String getName() {
        return this.text;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}
