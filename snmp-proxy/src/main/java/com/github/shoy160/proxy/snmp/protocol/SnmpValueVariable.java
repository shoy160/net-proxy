package com.github.shoy160.proxy.snmp.protocol;

import cn.hutool.core.convert.Convert;
import com.github.shoy160.proxy.util.BufferUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

import java.math.BigInteger;
import java.nio.charset.Charset;

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
    public final static int TYPE_LONG = 0x46;

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
            String[] ids = oid.substring(3).split(".");
            data = new byte[ids.length + 2];
            data[0] = 0x2B;
            data[1] = 0x06;
            for (int i = 0; i < ids.length; i++) {
                data[i + 2] = Convert.toByte(ids[i]);
            }
        } else if (getType() == TYPE_INT) {
            data = BufferUtils.intToBytes((int) value);
        } else if (getType() == TYPE_LONG) {
            data = BufferUtils.longToBytes((long) value);
        } else if (getType() == TYPE_STRING) {
            data = BufferUtils.stringToBytes(value.toString());
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
            this.value = BufferUtils.bytesToInt(this.valueBytes);
        } else if (this.getType() == TYPE_LONG) {
            this.value = BufferUtils.bytesToLong(this.valueBytes);
        } else if (this.getType() == TYPE_STRING) {
            this.value = BufferUtils.bytesToString(this.valueBytes, null);
        } else if (this.getType() == TYPE_OID) {
            StringBuilder sb = new StringBuilder("1.3.");
            for (int i = 2; i < getLength(); i++) {
                sb.append(String.format("%d.", valueBytes[i]));
            }
            this.value = sb.toString().substring(0, sb.length() - 1);
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
