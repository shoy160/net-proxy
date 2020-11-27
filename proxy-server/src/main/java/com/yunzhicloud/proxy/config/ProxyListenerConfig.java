package com.yunzhicloud.proxy.config;

import lombok.Getter;
import lombok.Setter;

/**
 * @author shay
 * @date 2020/11/27
 */
@Getter
@Setter
public class ProxyListenerConfig {
    private String localIp = "*";
    private Integer localPort = 23;
    private String remoteIp = "";
    private Integer remotePort = 23;

    @Override
    public String toString() {
        return String.format("%s:%d => %s:%d", this.localIp, this.localPort, this.remoteIp, this.remotePort);
    }
}
