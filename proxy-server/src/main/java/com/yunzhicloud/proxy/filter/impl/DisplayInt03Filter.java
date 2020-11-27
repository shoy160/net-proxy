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
public class DisplayInt03Filter extends RegexProxyFilter {

    private final static String REGEX = "^\\s+Input\\s+bandwidth\\s+utilization.*$";

    protected DisplayInt03Filter() {
        super(REGEX);
    }

    @Override
    protected void execute(byte[] data) {
        CommonUtils.replaceBytes(data, config.getInputPercent(), 35);
    }
}
