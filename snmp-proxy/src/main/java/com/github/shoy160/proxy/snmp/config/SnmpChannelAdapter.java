package com.github.shoy160.proxy.snmp.config;

import com.github.shoy160.proxy.adapter.ChannelAdapter;
import com.github.shoy160.proxy.snmp.protocol.SnmpV2c;
import com.github.shoy160.proxy.snmp.protocol.variable.SnmpOidVariable;
import com.github.shoy160.proxy.snmp.protocol.variable.SnmpValueVariable;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.util.ReferenceCountUtil;
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
    public ByteBuf onFrontend(ByteBuf buf, Channel front, Channel backend) {
        return buf;
    }

    @Override
    public void onBackendPipeline(ChannelPipeline pipeline) {

    }

    @Override
    public void onFrontendPipeline(ChannelPipeline pipeline) {

    }

    @Override
    public ByteBuf onBackend(ByteBuf buf, Channel front, Channel backend) {
        SnmpV2c pdu = new SnmpV2c();
        pdu.read(buf);
        log.info("pdu:{}", pdu);
        SnmpValueVariable value = pdu.getValue();
        SnmpOidVariable oid = pdu.getOid();
        if (value.getType() == SnmpValueVariable.TYPE_COUNTER64 && oid.getPort() == 7) {
            log.info("filter data");
            pdu.getValue().setValue(123456789L);
            ReferenceCountUtil.release(buf);
            return pdu.write();
        } else {
            buf.resetReaderIndex();
            return buf;
        }
    }
}
