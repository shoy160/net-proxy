package com.github.shoy160.snmp.test;

import com.github.shoy160.proxy.snmp.helper.SnmpHelper;
import com.github.shoy160.proxy.snmp.protocol.SnmpV2c;
import com.github.shoy160.proxy.util.BufferUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author shay
 * @date 2020/12/4
 */
@Slf4j
public class SnmpV2Test {
    private final static String SRC_HEX = "303502010104087075626c69633131a22602042bf64d4802010002010030183016060b2b060102011f0101010a08460701d822b275a319";

    @Test
    public void decodeTest() {
        byte[] bytes = ByteBufUtil.decodeHexDump(SRC_HEX);
        ByteBuf buf = Unpooled.wrappedBuffer(bytes);
        SnmpV2c pdu = new SnmpV2c();
        pdu.read(buf);
        log.info("pdu:{}", pdu);
        pdu.getValue().setValue(125L);
        ByteBuf t = pdu.write();
        log.info("after pdu:{}", ByteBufUtil.prettyHexDump(t));
    }

    @Test
    public void convertTest() {
        byte[] b = BufferUtils.fromLong(1252354);
        log.info("value:{}", b);
    }

    @Test
    public void oidTest() {
        byte[] data = new byte[]{0x2b, 0x06, 0x01, 0x02, 0x01, 0x1f, 0x01, 0x01, 0x01, 0x0a, -126, 0x3b};
        log.info("before hex:\t{}", BufferUtils.toHex(data));
        String oid = SnmpHelper.decodeObjectId(data);
        log.info("oid:\t{}", oid);
        oid = oid.replace(".31", ".325");
        data = SnmpHelper.encodeObjectId(oid);
        log.info("after hex:\t{}", BufferUtils.toHex(data));
        oid = SnmpHelper.decodeObjectId(data);
        log.info("oid:\t{}", oid);
    }
}
