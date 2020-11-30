/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.yunzhicloud.proxy.bean;

import com.yunzhicloud.proxy.config.ProxyListenerConfig;
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
                    .handler(new LoggingHandler(LogLevel.DEBUG))
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
