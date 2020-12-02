package com.github.shoy160.proxy;

import com.github.shoy160.proxy.config.ProxyConfig;
import com.github.shoy160.proxy.config.ProxyListenerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author shay
 * @date 2020/12/2
 */
@Slf4j
@Component
public class ProxyManager {

    @Autowired
    private ProxyConfig proxyConfig;

    private void startProxy(ProxyListenerConfig config) {
        log.info("正在启动:{}", config);
        try {
            HexDumpProxy proxy = new HexDumpProxy(config);
            Thread thread = new Thread(proxy);
            thread.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void execute() {
        ProxyListenerConfig[] proxyList = proxyConfig.getList();
        if (proxyList != null) {
            for (ProxyListenerConfig item : proxyList) {
                startProxy(item);
            }
        }
        if (proxyConfig.getConfig() != null) {
            startProxy(proxyConfig.getConfig());
        }
    }

}