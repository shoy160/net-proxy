package com.github.shoy160.proxy.snmp.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.Setter;

/**
 * @author shay
 * @date 2020/12/4
 */
@Getter
@Setter
public class SnmpV2c {
    private SnmpVariable type;
    private SnmpValueVariable version;
    private SnmpValueVariable community;
    private SnmpVariable method;
    private SnmpValueVariable requestId;
    private SnmpValueVariable errorState;
    private SnmpValueVariable errorIndex;
    private SnmpVariable dataType;
    private SnmpVariable vbs;
    private SnmpOidVariable oid;
    private SnmpValueVariable value;

    public SnmpV2c() {
        this.type = new SnmpVariable();
        this.version = new SnmpValueVariable();
        this.community = new SnmpValueVariable();
        this.method = new SnmpVariable();
        this.requestId = new SnmpValueVariable();
        this.errorState = new SnmpValueVariable();
        this.errorIndex = new SnmpValueVariable();
        this.dataType = new SnmpVariable();
        this.vbs = new SnmpVariable();
        this.oid = new SnmpOidVariable();
        this.value = new SnmpValueVariable();
    }

    public void read(ByteBuf buf) {
        this.type.read(buf);
        this.version.read(buf);
        this.community.read(buf);
        this.method.read(buf);
        this.requestId.read(buf);
        this.errorState.read(buf);
        this.errorIndex.read(buf);
        this.dataType.read(buf);
        this.vbs.read(buf);
        this.oid.read(buf);
        this.value.read(buf);
    }

    public ByteBuf write() {
        int length = 0;
        byte[] value = this.value.getBytes();
        length += value.length;
        byte[] oid = this.oid.getBytes();
        length += oid.length;
        this.vbs.setLength(length);
        byte[] vbs = this.vbs.getBytes();
        length += vbs.length;
        this.dataType.setLength(this.vbs.getLength() + 2);
        byte[] dataType = this.dataType.getBytes();
        byte[] errorIndex = this.errorIndex.getBytes();
        byte[] errorState = this.errorState.getBytes();
        byte[] requestId = this.requestId.getBytes();
        length += dataType.length + errorIndex.length + errorState.length + requestId.length;
        this.method.setLength(length);
        byte[] method = this.method.getBytes();
        length += method.length;
        byte[] community = this.community.getBytes();
        byte[] version = this.version.getBytes();
        length += community.length + version.length;
        this.type.setLength(length);
        byte[] type = this.type.getBytes();

        return Unpooled.wrappedBuffer(type, version, community, method, requestId, errorState, errorIndex, dataType, vbs, oid, value);
    }

    @Override
    public String toString() {
        return "SnmpV2c{" +
                "type=" + type +
                ", version=" + version +
                ", community=" + community +
                ", method=" + method +
                ", requestId=" + requestId +
                ", errorState=" + errorState +
                ", errorIndex=" + errorIndex +
                ", dataType=" + dataType +
                ", vbs=" + vbs +
                ", oid=" + oid +
                ", value=" + value +
                '}';
    }
}
