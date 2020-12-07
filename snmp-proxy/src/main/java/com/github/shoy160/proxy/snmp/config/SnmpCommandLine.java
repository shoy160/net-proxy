package com.github.shoy160.proxy.snmp.config;

import cn.hutool.core.util.HexUtil;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.*;
import org.snmp4j.asn1.BERInputStream;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * @author shay
 * @date 2020/12/3
 */
@Slf4j
//@Component
public class SnmpCommandLine implements CommandLineRunner {
    private final static int RETRY_COUNT = 5;
    private final static int TIMEOUT = 3000;

    private final static List<Integer> VERSIONS = new ArrayList<Integer>() {{
        add(SnmpConstants.version1);
        add(SnmpConstants.version2c);
        add(SnmpConstants.version3);
    }};

    private static Target createTarget(String ipAddress, int port, int version, String community) {
        Target target;
        if (!VERSIONS.contains(version)) {
            log.error("参数version异常");
            return null;
        }
        if (version == SnmpConstants.version3) {
            target = new UserTarget();
            //snmpV3需要设置安全级别和安全名称，其中安全名称是创建snmp指定user设置的new OctetString("SNMPV3")
            target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
            target.setSecurityName(new OctetString("SNMPV3"));
        } else {
            //snmpV1和snmpV2需要指定团体名名称
            target = new CommunityTarget();
            ((CommunityTarget) target).setCommunity(new OctetString(community));
            if (version == SnmpConstants.version2c) {
                target.setSecurityModel(SecurityModel.SECURITY_MODEL_SNMPv2c);
            }
        }
        target.setVersion(version);
        //必须指定，没有设置就会报错。
        target.setAddress(GenericAddress.parse(String.format("udp:%s/%d", ipAddress, port)));
        target.setRetries(RETRY_COUNT);
        target.setTimeout(TIMEOUT);
        return target;
    }

    private static PDU createPDU(int version, int type, OID oid) {
        PDU pdu;
        if (version == SnmpConstants.version3) {
            pdu = new ScopedPDU();
        } else {
            pdu = new PDUv1();
        }
        pdu.setType(type);
        //可以添加多个变量oid
        pdu.add(new VariableBinding(oid));
        return pdu;
    }

    private static Snmp createSnmp() throws IOException {
        MessageDispatcherImpl dispatcher = new MessageDispatcherImpl();
        dispatcher.addMessageProcessingModel(new MPv1());
        dispatcher.addMessageProcessingModel(new MPv2c());
        //当要支持snmpV3版本时，需要配置user
        OctetString octetString = new OctetString(MPv3.createLocalEngineID());
        USM usm = new USM(SecurityProtocols.getInstance().addDefaultProtocols(), octetString, 0);
        UsmUser user = new UsmUser(new OctetString("SNMPV3"), AuthSHA.ID, new OctetString("authPassword"),
                PrivAES128.ID, new OctetString("privPassword"));
        usm.addUser(user.getSecurityName(), user);
        dispatcher.addMessageProcessingModel(new MPv3(usm));

        //2、创建transportMapping
        TransportMapping<?> transportMapping = new DefaultUdpTransportMapping();
        //3、正式创建snmp
        Snmp snmp = new Snmp(dispatcher, transportMapping);
        //开启监听
        snmp.listen();
        return snmp;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("enter command");
//        String hex = "30 35 02 01 01 04 08 70 75 62 6C 69 63 31 31 A2 26 02 04 28 25 92 B7 02 01 00 02 01 00 30 18 30 16 06 0B 2B 06 01 02 01 1F 01 01 01 0A 08 46 07 01 BA 11 C2 CA 57 8D";
//        StringBuilder sb = new StringBuilder();
//        for (String t : hex.split("\\s")) {
//            sb.append(Integer.toBinaryString(Integer.parseInt(t, 16)));
//        }
//        log.info(sb.toString());
//        PDU pdu1 = new PDUv1();
////        pdu1.encodeBER();
////        pdu1.setType(PDU.RESPONSE);
//        byte[] bytes = HexUtil.decodeHex("30 35 02 01 01 04 08 70 75 62 6C 69 63 31 31 A2 26 02 04 71 3E B1 29 02 01 00 02 01 00 30 18 30 16 06 0B 2B 06 01 02 01 1F 01 01 01 0A 08 46 07 01 BA 8C DF AE 76 A1".replaceAll("\\s", ""));
//        BERInputStream stream = new BERInputStream(ByteBuffer.wrap(bytes));
//        pdu1.decodeBER(stream);
//        log.info("decode pdu:{}", pdu1);
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(3000);
//                Target target = createTarget("192.168.20.81", 161, SnmpConstants.version2c, "public11");
                Target target = createTarget("127.0.0.1", 8161, SnmpConstants.version2c, "public11");
                OID oid = new OID(".1.3.6.1.2.1.31.1.1.1.10.7");
                PDU pdu = createPDU(SnmpConstants.version2c, PDU.GETNEXT, oid);
                log.info("-------> 发送PDU <-------");
                //4、发送报文，并获取返回结果
                Snmp snmp = createSnmp();
                ResponseEvent responseEvent = snmp.send(pdu, target);
                PDU response = responseEvent.getResponse();
//                Vector<? extends VariableBinding> bindings = response.getVariableBindings();
//                for (VariableBinding binding : bindings) {
//                    binding.setVariable(new Counter64(488054242760000L));
//                }
                log.info("返回结果：{}", response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();

    }
}
