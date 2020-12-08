package com.yunzhicloud.proxy.filter.impl;

import com.github.shoy160.proxy.util.RegexUtils;
import com.github.shoy160.proxy.util.SpringUtils;
import com.yunzhicloud.proxy.config.TransferConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author shay
 * @date 2020/11/27
 */
@Slf4j
@Component
public class DisplayInterfaceFilter extends RegexProxyFilter {
    private final static String REGEX = "@ETH\\s+(up|down)\\s+(up|down)\\s+([^\\s]+\\s+[^\\s]+)";
    private final static String STATE_DOWN = "down";

    protected DisplayInterfaceFilter(TransferConfig config) {
        super(REGEX, config);
    }

    @Override
    protected String execute(String content) {
        String ethName = getGroup(1);
        String inputState = getGroup(2);
        String outputState = getGroup(3);
        log.info("state : {},{}", inputState, outputState);
        if (!"Ethernet0/0/0".equals(ethName)) {
            return content;
        }
        if (STATE_DOWN.equals(inputState) && STATE_DOWN.equals(outputState)) {
            return content;
        }
        double input = config.getInputRate() * 100D / (config.getTotal() * 1024 * 1024 * 8);
        double output = config.getOutputRate() * 100D / (config.getTotal() * 1024 * 1024 * 8);
        String replacement = String.format("%.2f%%  %.2f%%", input, output);
        return RegexUtils.replace(getPattern(), content, 4, replacement);
    }
}
