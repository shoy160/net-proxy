package com.github.shoy160.proxy.snmp.protocol.variable;

import com.yunzhicloud.core.utils.CommonUtils;

/**
 * @author shay
 * @date 2020/12/7
 */
public class SnmpOidVariable extends SnmpValueVariable {
    private final static String SNMP_IN = "1.3.6.1.2.1.31.1.1.1.6.";
    private final static String SNMP_OUT = "1.3.6.1.2.1.31.1.1.1.10.";

    public String getOid() {
        if (this.getValue() == null) {
            return null;
        }
        return this.getValue().toString();
    }

    public boolean isOutput() {
        String oid = getOid();
        if (CommonUtils.isEmpty(oid)) {
            return false;
        }
        return oid.startsWith(SNMP_OUT);
    }

    public boolean isInput() {
        String oid = getOid();
        if (CommonUtils.isEmpty(oid)) {
            return false;
        }
        return oid.startsWith(SNMP_IN);
    }

    public int getPort() {
        String oid = getOid();
        if (CommonUtils.isEmpty(oid)) {
            return 0;
        }
        String[] array = oid.split("\\.");
        if (array.length <= 1) {
            return 0;
        }
        return new Integer(array[array.length - 1]);
    }
}
