package com.github.shoy160.proxy;

import com.github.shoy160.proxy.config.ProxyListenerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author shay
 * @date 2020/12/2
 */
@Slf4j
public final class HexDumpProxy implements Runnable {

    private final ProxyListenerConfig config;
    private final static String ANY_IP = "*";

    public HexDumpProxy(ProxyListenerConfig config) {
        this.config = config;
    }

    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new HexDumpProxyInitializer(this.config.getRemoteIp(), this.config.getRemotePort()))
                    .childOption(ChannelOption.AUTO_READ, false);

            if (ANY_IP.equals(this.config.getLocalIp())) {
                b.bind(this.config.getLocalPort()).sync().channel().closeFuture().sync();
            } else {
                b.bind(this.config.getLocalIp(), this.config.getLocalPort()).sync().channel().closeFuture().sync();

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
