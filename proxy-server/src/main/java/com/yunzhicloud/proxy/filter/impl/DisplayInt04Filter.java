package com.yunzhicloud.proxy.filter.impl;

import com.yunzhicloud.proxy.util.BufferUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author shay
 * @date 2020/11/27
 */
@Slf4j
@Component
public class DisplayInt04Filter extends RegexProxyFilter {

    private final static String REGEX = "^(\\r\\n)?.*Output\\s+bandwidth\\s+utilization.*$";

    protected DisplayInt04Filter() {
        super(REGEX);
    }

    @Override
    protected byte[] execute(byte[] data) {
        BufferUtils.replaceBytes(data, config.getOutputPercent(), 37);
        return data;
    }
}
