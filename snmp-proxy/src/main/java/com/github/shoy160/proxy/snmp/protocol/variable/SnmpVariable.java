package com.github.shoy160.proxy.snmp.protocol.variable;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

/**
 * @author shay
 * @date 2020/12/4
 */
@Getter
@Setter
public class SnmpVariable implements Variable {
    private int type;
    private int length;

    @Override
    public void read(ByteBuf buf) {
        this.type = buf.readUnsignedByte();
        this.length = buf.readUnsignedByte();
    }

    @Override
    public byte[] getBytes() {
        byte[] data = new byte[2];
        data[0] = (byte) this.type;
        data[1] = (byte) this.length;
        return data;
    }

    @Override
    public String toString() {
        return String.format("[%d,%d]", this.type, this.length);
    }
}
