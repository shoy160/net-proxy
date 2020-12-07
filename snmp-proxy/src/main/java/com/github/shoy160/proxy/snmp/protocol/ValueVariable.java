package com.github.shoy160.proxy.snmp.protocol;

/**
 * @author shay
 * @date 2020/12/4
 */
public interface ValueVariable extends Variable {
    /**
     * 获取值
     *
     * @return value
     */
    Object getValue();

    /**
     * 设置值
     *
     * @param obj value
     */
    void setValue(Object obj);
}
