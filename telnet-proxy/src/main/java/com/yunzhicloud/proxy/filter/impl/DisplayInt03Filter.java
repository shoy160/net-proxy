package com.yunzhicloud.proxy.filter.impl;

import com.github.shoy160.proxy.util.RegexUtils;
import com.yunzhicloud.proxy.config.TransferConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author shay
 * @date 2020/11/27
 */
@Slf4j
@Component
public class DisplayInt03Filter extends RegexProxyFilter {

    private final static String REGEX = "Input\\s+bandwidth\\s+utilization\\s+:\\s+([0-9]+\\.[0-9]+%)";

    protected DisplayInt03Filter(TransferConfig config) {
        super(REGEX, config);
    }

    @Override
    protected String execute(String data) {
        if (config.getTotal() <= 0) {
            return data;
        }
        double percent = (config.getInputRate() * 100D) / (config.getTotal() * 1024 * 1024 * 8);
        return RegexUtils.replace(getPattern(), data, 1, String.format("%.2f%%", percent));
    }
}
