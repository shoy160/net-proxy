package com.github.shoy160.proxy;

import com.github.shoy160.proxy.adapter.ChannelAdapter;
import com.github.shoy160.proxy.config.ProtocolType;
import com.github.shoy160.proxy.config.ProxyListenerConfig;
import com.github.shoy160.proxy.handler.TcpProxyFrontendHandler;
import com.github.shoy160.proxy.handler.UdpProxyFrontendHandler;
import com.github.shoy160.proxy.util.SpringUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

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

    private void startTcpListener() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new HexDumpProxyInitializer(this.config))
                    .childOption(ChannelOption.AUTO_READ, false);

            ChannelFuture channelFuture;
            if (ANY_IP.equals(this.config.getLocalIp())) {
                channelFuture = bootstrap.bind(this.config.getLocalPort()).sync();
            } else {
                channelFuture = bootstrap.bind(this.config.getLocalIp(), this.config.getLocalPort()).sync();
            }
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private void startUdpListener() {
        EventLoopGroup group = new NioEventLoopGroup();
        InetSocketAddress address;
        if (ANY_IP.equals(this.config.getLocalIp())) {
            address = new InetSocketAddress(this.config.getLocalPort());
        } else {
            address = new InetSocketAddress(this.config.getLocalIp(), this.config.getLocalPort());
        }
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioDatagramChannel.class)
                    .localAddress(address)
                    .handler(new UdpProxyFrontendHandler(this.config));

            ChannelFuture channelFuture = bootstrap.bind(address).sync();
            channelFuture.channel().closeFuture().await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    @Override
    public void run() {
        if (this.config.getType() == ProtocolType.TCP) {
            startTcpListener();
        } else if (this.config.getType() == ProtocolType.UDP) {
            startUdpListener();
        } else {
            log.warn("Not Support Protocol:{}", this.config.getType());
        }
    }
}
