package com.yunzhicloud.proxy.filter.impl;

import com.yunzhicloud.proxy.config.ProxyConfig;
import com.yunzhicloud.proxy.filter.ProxyFilter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @author shay
 * @date 2020/11/27
 */
@Slf4j
public abstract class RegexProxyFilter implements ProxyFilter {
    private final String regex;

    @Resource
    protected ProxyConfig config;

    protected RegexProxyFilter(String regex) {
        this.regex = regex;
    }

    @Override
    public boolean isMatch(String content) {
        return content.matches(this.regex);
    }

    @Override
    public void invoke(byte[] data) {
        log.info("{} invoke", getClass().getSimpleName());
        execute(data);
    }

    /**
     * 执行过滤
     *
     * @param data 数据源
     */
    protected abstract void execute(byte[] data);
}
