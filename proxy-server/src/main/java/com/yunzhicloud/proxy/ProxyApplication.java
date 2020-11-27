package com.yunzhicloud.proxy;

import com.yunzhicloud.proxy.bean.ProxyManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author shay
 */
@SpringBootApplication
public class ProxyApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ProxyApplication.class, args);
        applicationContext.registerShutdownHook();
        ProxyManager proxyManager = applicationContext.getBean(ProxyManager.class);
        proxyManager.execute();
    }
}
