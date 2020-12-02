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
     * @param data       内容
     * @param eth        网卡名称
     * @param startIndex 开始位置
     * @return boolean
     */
    boolean isMatch(String data, String eth, Integer startIndex);

    /**
     * 数据转换
     *
     * @return byte[]
     */
    String transfer();
}
