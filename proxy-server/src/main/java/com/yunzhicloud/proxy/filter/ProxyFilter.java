package com.yunzhicloud.proxy.filter;

/**
 * 代理过滤器
 *
 * @author shay
 * @date 2020/11/27
 */
public interface ProxyFilter {

    /**
     * 是否匹配规则
     *
     * @param content 内容
     * @return boolean
     */
    boolean isMatch(String content);

    /**
     * 数据转换
     *
     * @param data 数据源
     */
    void invoke(byte[] data);
}
