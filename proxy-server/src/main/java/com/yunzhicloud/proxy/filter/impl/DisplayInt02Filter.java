package com.yunzhicloud.proxy.filter.impl;

import com.yunzhicloud.proxy.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author shay
 * @date 2020/11/27
 */
@Slf4j
@Component
public class DisplayInt02Filter extends RegexProxyFilter {

    private final static String REGEX = "^\\.\\.Last\\s+5\\s+minutes\\s+output\\s+rate.*$";

    protected DisplayInt02Filter() {
        super(REGEX);
    }

    @Override
    protected void execute(byte[] data) {

        CommonUtils.replaceBytes(data, config.getOutputRate(), 29);
        CommonUtils.replaceBytes(data, config.getOutputPackets(), 43);
    }
}
