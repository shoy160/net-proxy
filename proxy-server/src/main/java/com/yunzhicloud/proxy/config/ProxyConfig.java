package com.yunzhicloud.proxy.config;

import io.netty.handler.logging.LogLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

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
    private String inputRate;
    private String inputPackets;
    private String outputRate;
    private String outputPackets;
    private String inputPercent;
    private String outputPercent;
}
