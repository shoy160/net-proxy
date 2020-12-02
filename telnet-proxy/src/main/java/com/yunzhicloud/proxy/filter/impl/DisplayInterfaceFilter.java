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
public class DisplayInterfaceFilter extends RegexProxyFilter {
    private final static String NET0_REGEX = "Ethernet0/0/0\\s+up\\s+up\\s+([^\\s]+\\s+[^\\s]+)";

    protected DisplayInterfaceFilter() {
        super(NET0_REGEX);
    }

    @Override
    protected String execute(String content) {
        double input = config.getInputRate() * 100D / config.getTotal();
        double output = config.getOutputRate() * 100D / config.getTotal();
        String replacement = String.format("%.2f%%  %.2f%%", input, output);
        return RegexUtils.replace(NET0_REGEX, content, 1, replacement);
    }
}
