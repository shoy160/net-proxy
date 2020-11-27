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
public class DisplayInt01Filter extends RegexProxyFilter {

    private final static String REGEX = "^\\r\\nLast\\s+5\\s+minutes\\s+input\\s+rate.*$";

    protected DisplayInt01Filter() {
        super(REGEX);
    }

    @Override
    protected byte[] execute(byte[] data) {
        BufferUtils.replaceBytes(data, config.getInputRate(), 29);
        BufferUtils.replaceBytes(data, config.getInputPackets(), 43);
        return data;
    }
}
