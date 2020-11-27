package com.yunzhicloud.proxy.filter.impl;

import com.yunzhicloud.proxy.config.ProxyConfig;
import com.yunzhicloud.proxy.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author shay
 * @date 2020/11/27
 */
@Slf4j
@Component
public class DisplayInt01Filter extends RegexProxyFilter {

    private final static String REGEX = "^\\.\\.Last\\s+5\\s+minutes\\s+input\\s+rate.*$";

    protected DisplayInt01Filter() {
        super(REGEX);
    }

    @Override
    protected void execute(byte[] data) {
        CommonUtils.replaceBytes(data, config.getInputRate(), 29);
        CommonUtils.replaceBytes(data, config.getInputPackets(), 43);
    }
}
