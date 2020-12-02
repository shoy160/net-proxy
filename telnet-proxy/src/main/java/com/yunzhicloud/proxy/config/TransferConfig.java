package com.yunzhicloud.proxy.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author shay
 * @date 2020/11/27
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "transfer")
public class TransferConfig {
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
     * 总带宽 MB
     */
    private Long total = 160L;

    private String regex;
    private String interfaceRegex = "(Eth-Trunk[0-9]+|Route-Aggregation[0-9]+|XGE[0-9]+/[0-9]+/[0-9]+/[0-9]+|Ethernet[0-9]+)";
}
