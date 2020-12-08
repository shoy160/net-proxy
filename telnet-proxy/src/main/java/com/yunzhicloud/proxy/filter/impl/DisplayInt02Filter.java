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
public class DisplayInt02Filter extends RegexProxyFilter {

    private final static String REGEX = "Last\\s+(?:(?:5\\s+minutes)|(?:300\\s+seconds))\\s+output\\s+rate\\s+([0-9]+\\s+[a-z]+/sec,\\s[0-9]+\\s[a-z]+/sec)";

    protected DisplayInt02Filter(TransferConfig config) {
        super(REGEX, config);
    }

    @Override
    protected String execute(String data) {
        String replacement = String.format("%d bits/sec, %d packets/sec", config.getOutputRate(), config.getOutputRate() / 7000);
        return RegexUtils.replace(getPattern(), data, 1, replacement);
    }
}
