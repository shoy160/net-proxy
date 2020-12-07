package com.github.shoy160.proxy;

import com.github.shoy160.proxy.adapter.ChannelAdapter;
import com.github.shoy160.proxy.handler.TcpProxyFrontendHandler;
import com.github.shoy160.proxy.util.SpringUtils;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author shay
 * @date 2020/12/2
 */
public class HexDumpProxyInitializer extends ChannelInitializer<SocketChannel> {

    private final String remoteHost;
    private final int remotePort;

    public HexDumpProxyInitializer(String remoteHost, int remotePort) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelAdapter adapter = SpringUtils.getObject(ChannelAdapter.class);
        ch.pipeline()
                .addLast(new LoggingHandler(LogLevel.INFO));
        if (adapter != null) {
            adapter.onFrontendPipeline(ch.pipeline());
        }
        ch.pipeline().addLast(new TcpProxyFrontendHandler(remoteHost, remotePort));
    }
}
