package com.github.shoy160.proxy.snmp;

import com.github.shoy160.proxy.ProxyManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author shay
 * @date 2020/12/2
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.github.shoy160.proxy")
public class SnmpApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(SnmpApplication.class, args);
        applicationContext.registerShutdownHook();
        ProxyManager proxyManager = applicationContext.getBean(ProxyManager.class);
        proxyManager.execute();
    }
}
