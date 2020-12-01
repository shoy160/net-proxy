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
package com.yunzhicloud.proxy.handler;

import com.yunzhicloud.proxy.filter.TransferManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author shay
 */
@Slf4j
public class HexDumpProxyBackendHandler extends BaseHandlerAdapter {

    private final Channel inboundChannel;
    private final TransferManager transferManager;

    public HexDumpProxyBackendHandler(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
        transferManager = new TransferManager();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.read();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        msg = transferManager.transferMsg((ByteBuf) msg, ctx.channel());
        inboundChannel
                .writeAndFlush(msg)
                .addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        ctx.channel().read();
                    } else {
                        future.channel().close();
                    }
                });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        HexDumpProxyFrontendHandler.closeOnFlush(inboundChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        HexDumpProxyFrontendHandler.closeOnFlush(ctx.channel());
    }
}
