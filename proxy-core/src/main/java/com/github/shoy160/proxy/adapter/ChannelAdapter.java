package com.github.shoy160.proxy.adapter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;

/**
 * @author shay
 * @date 2020/12/2
 */
public interface ChannelAdapter {
    /**
     * 输入消息处理
     *
     * @param buf     buf
     * @param front   front
     * @param backend backend
     * @return msg
     */
    ByteBuf onFrontend(ByteBuf buf, Channel front, Channel backend);

    /**
     * 输出流程线
     *
     * @param pipeline pipeline
     */
    void onBackendPipeline(ChannelPipeline pipeline);

    /**
     * 输出流程线
     *
     * @param pipeline pipeline
     */
    void onFrontendPipeline(ChannelPipeline pipeline);


    /**
     * 输出消息处理
     *
     * @param buf     buf
     * @param front   front
     * @param backend backend
     * @return msg
     */
    ByteBuf onBackend(ByteBuf buf, Channel front, Channel backend);
}
