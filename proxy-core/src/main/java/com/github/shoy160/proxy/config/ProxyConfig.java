package com.github.shoy160.proxy.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * @author shay
 * @date 2020/12/2
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "proxy")
public class ProxyConfig {
    private ProxyListenerConfig[] list;
    @NestedConfigurationProperty
    private ProxyListenerConfig config;
}
