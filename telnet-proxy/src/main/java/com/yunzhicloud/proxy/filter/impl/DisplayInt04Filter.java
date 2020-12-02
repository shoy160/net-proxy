package com.yunzhicloud.proxy.filter.impl;

import com.github.shoy160.proxy.util.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author shay
 * @date 2020/11/27
 */
@Slf4j
@Component
public class DisplayInt04Filter extends RegexProxyFilter {

    private final static String REGEX = "Output\\s+bandwidth\\s+utilization\\s+:\\s+([0-9]+\\.[0-9]+%)";

    protected DisplayInt04Filter() {
        super(REGEX);
    }

    @Override
    protected String execute(String data) {
        double percent = config.getOutputRate() * 100D / (config.getTotal() * 1024 * 1024 * 1024 * 8);
        return RegexUtils.replace(REGEX, data, 1, String.format("%.2f%%", percent));
    }
}
