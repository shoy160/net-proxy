package com.github.shoy160.proxy.snmp.protocol.variable;

import io.netty.buffer.ByteBuf;

/**
 * @author shay
 * @date 2020/12/4
 */
public interface Variable {

    /**
     * 获取类型
     *
     * @return 类型
     */
    int getType();

    /**
     * 读数据
     *
     * @param buf buf
     */
    void read(ByteBuf buf);

    /**
     * 写入到
     *
     * @return byte[]
     */
    byte[] getBytes();
}
