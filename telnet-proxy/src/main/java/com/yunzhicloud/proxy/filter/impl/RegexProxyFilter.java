package com.yunzhicloud.proxy.filter.impl;

import com.yunzhicloud.core.utils.CommonUtils;
import com.yunzhicloud.proxy.config.TransferConfig;
import com.yunzhicloud.proxy.filter.ProxyFilter;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shay
 * @date 2020/11/27
 */
@Slf4j
public abstract class RegexProxyFilter implements ProxyFilter {
    private final static String ETH_SPACE = "@ETH";
    private final Pattern pattern;
    private String content;
    private String eth;
    private Matcher matcher;


    /**
     * 获取网卡名称
     *
     * @return eth
     */
    protected String getEth() {
        return this.eth;
    }

    protected Pattern getPattern() {
        return this.pattern;
    }

    protected String getGroup(int group) {
        if (this.matcher != null && this.matcher.groupCount() >= group) {
            return matcher.group(group);
        }
        return "";
    }

    protected TransferConfig config;

    protected RegexProxyFilter(String regex, TransferConfig config) {
        this.config = config;
        if (regex.contains(ETH_SPACE)) {
            regex = regex.replace(ETH_SPACE, config.getInterfaceRegex());
        }
        log.info("{} regex:{}", getClass().getSimpleName(), regex);
        this.pattern = Pattern.compile(regex, Pattern.DOTALL);
    }

    @Override
    public boolean isMatch(String data, String eth, Integer startIndex) {
        if (CommonUtils.isEmpty(data)) {
            return false;
        }
        this.matcher = this.pattern.matcher(data);
        while (this.matcher.find()) {
            if (this.matcher.start(1) < startIndex) {
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
