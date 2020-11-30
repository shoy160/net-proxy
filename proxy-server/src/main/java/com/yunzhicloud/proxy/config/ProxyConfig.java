package com.yunzhicloud.proxy.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * @author shay
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "proxy")
public class ProxyConfig {
    private ProxyListenerConfig[] list;
    @NestedConfigurationProperty
    private ProxyListenerConfig config;

    /**
     * 设备ID
     */
    private String deviceId;
    /**
     * 入网流量 bits
     */
    private Long inputRate;
    /**
     * 出网流量 bits
     */
    private Long outputRate;
    /**
     * 总带宽 GB
     */
    private Long total = 160L;
}
