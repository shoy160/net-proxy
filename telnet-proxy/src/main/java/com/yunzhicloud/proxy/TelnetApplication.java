package com.yunzhicloud.proxy;

import com.github.shoy160.proxy.ProxyManager;
import com.github.shoy160.proxy.util.SpringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author shay
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.yunzhicloud.proxy", "com.github.shoy160.proxy"})
public class TelnetApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(TelnetApplication.class, args);
        applicationContext.registerShutdownHook();
        SpringUtils.setContext(applicationContext);
        ProxyManager proxyManager = applicationContext.getBean(ProxyManager.class);
        proxyManager.execute();
    }
}
