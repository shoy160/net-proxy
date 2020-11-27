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
public class DisplayInterfaceFilter extends RegexProxyFilter {
    private final static String NET0_REGEX = "^\\.\\.Ethernet0/0/0\\s+up\\s+up.*$";

    protected DisplayInterfaceFilter() {
        super(NET0_REGEX);
    }

    @Override
    protected void execute(byte[] data) {
        //InUti
        CommonUtils.replaceBytes(data, config.getInputPercent(), 46);
        //OutUti
        CommonUtils.replaceBytes(data, config.getOutputPercent(), 53);
    }
}
