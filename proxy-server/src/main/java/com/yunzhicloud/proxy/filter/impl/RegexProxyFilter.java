package com.yunzhicloud.proxy.filter.impl;

import cn.hutool.core.util.ReUtil;
import com.yunzhicloud.core.utils.CommonUtils;
import com.yunzhicloud.proxy.config.ProxyConfig;
import com.yunzhicloud.proxy.filter.ProxyFilter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shay
 * @date 2020/11/27
 */
@Slf4j
public abstract class RegexProxyFilter implements ProxyFilter {
    private final String regex;
    private String content;
    private String eth;

    /**
     * 获取网卡名称
     *
     * @return eth
     */
    protected String getEth() {
        return this.eth;
    }

    @Resource
    protected ProxyConfig config;

    protected RegexProxyFilter(String regex) {
        this.regex = regex;
    }

    @Override
    public boolean isMatch(String data, String eth, Integer startIndex) {
        if (CommonUtils.isEmpty(this.regex) || CommonUtils.isEmpty(data)) {
            return false;
        }
        Matcher matcher = Pattern.compile(this.regex, Pattern.DOTALL).matcher(data);
        while (matcher.find()) {
            if (matcher.start(1) < startIndex) {
                continue;
            }
            this.content = data;
            this.eth = eth;
            return true;
        }
        return false;
    }

    @Override
    public String transfer() {
        log.info("{} invoke,{}", getClass().getSimpleName(), this.eth);
        return execute(this.content);
    }

    /**
     * 执行过滤
     *
     * @param content 数据源
     * @return byte[]
     */
    protected abstract String execute(String content);
}
