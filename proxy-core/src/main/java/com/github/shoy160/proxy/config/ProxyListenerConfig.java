package com.github.shoy160.proxy.config;

import com.github.shoy160.proxy.Constants;
import com.github.shoy160.proxy.adapter.ChannelAdapter;
import com.github.shoy160.proxy.util.SpringUtils;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author shay
 * @date 2020/12/2
 */
@Getter
@Setter
@Slf4j
public class ProxyListenerConfig {
    private ProtocolType type = ProtocolType.TCP;
    private String localIp = "*";
    private Integer localPort = 23;
    private String remoteIp = "";
    private Integer remotePort = 23;
    private Class<? extends ChannelAdapter> adapterClassName;

    @Override
    public String toString() {
        return String.format("%s://%s:%d => %s:%d", this.type.getName().toLowerCase(), this.localIp, this.localPort, this.remoteIp, this.remotePort);
    }

    public void attrAdapter(Channel channel) {
        if (this.adapterClassName != null) {
            ChannelAdapter adapter = SpringUtils.getObject(this.adapterClassName);
            if (adapter != null) {
                log.info("add adapter => {}", this.adapterClassName.getName());
                channel.attr(Constants.ATTR_ADAPTER).set(adapter);
            }
        }
    }
}
