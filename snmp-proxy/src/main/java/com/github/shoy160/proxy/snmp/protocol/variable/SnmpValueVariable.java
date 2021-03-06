package com.github.shoy160.proxy.snmp.protocol.variable;

import com.github.shoy160.proxy.snmp.helper.SnmpHelper;
import com.github.shoy160.proxy.util.BufferUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

/**
 * @author shay
 * @date 2020/12/4
 */
public class SnmpValueVariable extends SnmpVariable implements ValueVariable {
    private Object value;
    private byte[] valueBytes;

    public final static int TYPE_INT = 0x02;
    public final static int TYPE_STRING = 0x04;
    public final static int TYPE_OID = 0x06;
    public final static int TYPE_COUNTER = 0x41;
    public final static int TYPE_COUNTER64 = 0x46;

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
        if (value == null) {
            this.valueBytes = new byte[0];
            this.setLength(0);
            return;
        }
        byte[] data;
        if (getType() == TYPE_OID) {
            String oid = this.value.toString();
            data = SnmpHelper.encodeObjectId(oid);
        } else if (getType() == TYPE_INT) {
            data = BufferUtils.fromInt((int) value);
        } else if (getType() == TYPE_COUNTER64) {
            data = BufferUtils.fromLong((long) value);
        } else if (getType() == TYPE_STRING) {
            data = BufferUtils.fromString(value.toString());
        } else {
            data = valueBytes;
        }
        this.setLength(data.length);
        this.valueBytes = data;
    }

    @Override
    public void read(ByteBuf buf) {
        super.read(buf);
        ByteBuf valueBuf = buf.readSlice(this.getLength());
        this.valueBytes = ByteBufUtil.getBytes(valueBuf);
        if (this.getType() == TYPE_INT) {
            this.value = BufferUtils.toInt(this.valueBytes);
        } else if (this.getType() == TYPE_COUNTER64) {
            this.value = BufferUtils.toLong(this.valueBytes);
        } else if (this.getType() == TYPE_STRING) {
            this.value = BufferUtils.toString(this.valueBytes, null);
        } else if (this.getType() == TYPE_OID) {
            this.value = SnmpHelper.decodeObjectId(this.valueBytes);
        } else {
            this.value = valueBytes;
        }
    }

    @Override
    public byte[] getBytes() {
        byte[] data = new byte[this.getLength() + 2];
        data[0] = (byte) this.getType();
        data[1] = (byte) this.getLength();
        if (this.getLength() >= 0) {
            System.arraycopy(this.valueBytes, 0, data, 2, this.getLength());
        }
        return data;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[%d,%d]", this.getType(), this.getLength()));
        if (value != null) {
            sb.append(String.format("=>%s", this.getValue()));
        }
        return sb.toString();
    }
}
