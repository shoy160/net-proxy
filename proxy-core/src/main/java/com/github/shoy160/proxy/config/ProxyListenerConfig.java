package com.github.shoy160.proxy.config;

import lombok.Getter;
import lombok.Setter;

/**
 * @author shay
 * @date 2020/12/2
 */
@Getter
@Setter
public class ProxyListenerConfig {
    private ProtocolType type = ProtocolType.TCP;
    private String localIp = "*";
    private Integer localPort = 23;
    private String remoteIp = "";
    private Integer remotePort = 23;

    @Override
    public String toString() {
        return String.format("%s://%s:%d => %s:%d", this.type.getName().toLowerCase(), this.localIp, this.localPort, this.remoteIp, this.remotePort);
    }
}
