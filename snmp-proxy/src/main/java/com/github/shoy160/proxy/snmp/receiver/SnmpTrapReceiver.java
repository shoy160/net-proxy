package com.github.shoy160.proxy.snmp.receiver;

import lombok.extern.slf4j.Slf4j;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.*;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.PDUFactory;
import org.snmp4j.util.ThreadPool;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Vector;

/**
 * @author shay
 * @date 2020/12/10
 */
@Slf4j
//@Component
public class SnmpTrapReceiver implements CommandResponder, CommandLineRunner {

    @Override
    public void processPdu(CommandResponderEvent event) {
        if (event != null && event.getPDU() != null) {
            PDU requestPdu = event.getPDU();
            if (requestPdu == null) {
                return;
            }
            String community = new String(event.getSecurityName());
            String version = requestPdu instanceof ScopedPDU ? "v3" : (requestPdu instanceof PDUv1 ? "v1" : "v2c");

            // 需要确认trap
            if (requestPdu.getType() == PDU.INFORM) {
                PDU responsePdu = new PDU(requestPdu);
                responsePdu.setErrorIndex(0);
                responsePdu.setErrorStatus(0);
                responsePdu.setType(PDU.RESPONSE);
                StatusInformation statusInfo = new StatusInformation();
                StateReference stateRef = event.getStateReference();
                try {
                    event.getMessageDispatcher().returnResponsePdu(
                            event.getMessageProcessingModel(),
                            event.getSecurityModel(),
                            event.getSecurityName(),
                            event.getSecurityLevel(), responsePdu,
                            event.getMaxSizeResponsePDU(), stateRef,
                            statusInfo);

                } catch (MessageException msgEx) {
                    msgEx.printStackTrace();
                }
            }

            Vector<VariableBinding> recVBs = (Vector<VariableBinding>) event.getPDU()
                    .getVariableBindings();
            for (int i = 0; i < recVBs.size(); i++) {
                VariableBinding recVB = recVBs.elementAt(i);
                log.info("receiver version:{}, community:{} => {} : {}", version, community, recVB.getOid(), recVB.getVariable());
            }
        }
    }

    private Snmp createSnmp() throws IOException {
        ThreadPool threadPool = ThreadPool.create("Trap", 2);
        MultiThreadedMessageDispatcher dispatcher = new MultiThreadedMessageDispatcher(threadPool,
                new MessageDispatcherImpl());

        UdpAddress listenAddress = new UdpAddress(162);

        TransportMapping<?> transport = new DefaultUdpTransportMapping(listenAddress);
        Snmp snmp = new Snmp(dispatcher, transport);
        MessageDispatcher messageDispatcher = snmp.getMessageDispatcher();
        messageDispatcher.addMessageProcessingModel(new MPv1());
        messageDispatcher.addMessageProcessingModel(new MPv2c());
        messageDispatcher.addMessageProcessingModel(new MPv3());
        SecurityModels securityModels = SecurityModels.getInstance();
        USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(
                MPv3.createLocalEngineID()), 0);
        securityModels.addSecurityModel(usm);
        snmp.listen();
        return snmp;
    }

    @Override
    public void run(String... args) {
        try {
            Snmp snmp = createSnmp();
            snmp.addCommandResponder(this);
            log.info("snmp trap start listener at 162");
            Thread.sleep(1200);
            sendTrap();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void sendTrap() throws IOException {
        TransportMapping<UdpAddress> transport = new DefaultUdpTransportMapping();
        Snmp snmp = new Snmp(transport);
        transport.listen();

        PDU trap = new PDU();
        VariableBinding v = new VariableBinding();
        v.setOid(SnmpConstants.sysName);
        v.setVariable(new OctetString("Snmp Trap V2 Test"));
        trap.add(v);
        trap.setType(PDU.TRAP);

        // set target
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("sjhxkdl"));
        Address targetAddress = GenericAddress.parse("udp:192.168.2.14/162");
        target.setAddress(targetAddress);
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version2c);
        // send pdu, return response
        ResponseEvent event = snmp.send(trap, target);
//        PDU response = event.getResponse();
//        log.info("sender response:{}", response);
        transport.close();
        snmp.close();
    }
}
