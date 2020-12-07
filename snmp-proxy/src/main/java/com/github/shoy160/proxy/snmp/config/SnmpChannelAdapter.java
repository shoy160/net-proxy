package com.github.shoy160.proxy.snmp.config;

import com.github.shoy160.proxy.adapter.ChannelAdapter;
import com.github.shoy160.proxy.snmp.protocol.SnmpOidVariable;
import com.github.shoy160.proxy.snmp.protocol.SnmpV2c;
import com.github.shoy160.proxy.snmp.protocol.SnmpValueVariable;
import com.github.shoy160.proxy.snmp.protocol.ValueVariable;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author shay
 * @date 2020/12/2
 */
@Slf4j
@Component
public class SnmpChannelAdapter implements ChannelAdapter {
    @Override
    public ByteBuf onFrontend(ByteBuf buf, Channel channel) {
        return buf;
    }

    @Override
    public void onBackendPipeline(ChannelPipeline pipeline) {

    }

    @Override
    public void onFrontendPipeline(ChannelPipeline pipeline) {

    }

    @Override
    public ByteBuf onBackend(ByteBuf buf, Channel channel) {
        log.info("filter data");
        SnmpV2c pdu = new SnmpV2c();
        pdu.read(buf);
        log.info("pdu:{}", pdu);
        SnmpValueVariable value = pdu.getValue();
        SnmpOidVariable oid = pdu.getOid();
        if (value.getType() == SnmpValueVariable.TYPE_LONG && oid.getPort() == 7) {
            pdu.getValue().setValue(123456789L);
            return pdu.write();
        } else {
            buf.resetReaderIndex();
            return buf;
        }
    }
}
