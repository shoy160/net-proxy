package com.github.shoy160.proxy;

import com.github.shoy160.proxy.config.ProxyListenerConfig;
import com.github.shoy160.proxy.handler.TcpProxyFrontendHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author shay
 * @date 2020/12/2
 */
public class HexDumpProxyInitializer extends ChannelInitializer<SocketChannel> {

    private final ProxyListenerConfig config;

    public HexDumpProxyInitializer(ProxyListenerConfig config) {
        this.config = config;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new LoggingHandler(LogLevel.INFO));
        if (ch.hasAttr(Constants.ATTR_ADAPTER)) {
            ch.attr(Constants.ATTR_ADAPTER).get().onFrontendPipeline(pipeline);
        }
        pipeline.addLast(new TcpProxyFrontendHandler(this.config));
    }
}
