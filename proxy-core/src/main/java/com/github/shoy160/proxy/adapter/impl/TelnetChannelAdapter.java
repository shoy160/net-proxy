package com.github.shoy160.proxy.adapter.impl;

import com.github.shoy160.proxy.Constants;
import com.github.shoy160.proxy.adapter.ChannelAdapter;
import com.github.shoy160.proxy.util.BufferUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shay
 * @date 2020/12/2
 */
@Slf4j
public abstract class TelnetChannelAdapter implements ChannelAdapter {
    private final static Pattern HOST_REG = Pattern.compile("[\\r\\n]?(<[^>]+>|[0-9A-Z_]+[#])", Pattern.DOTALL);

    private final byte[] SHELL = new byte[]{0x0d, 0x00};
    private final byte[] CMD = new byte[]{0x0d, 0x0a};
    private final byte[] X_SHELL = new byte[]{0x0d};

    @Override
    public ByteBuf onFrontend(ByteBuf buf, Channel front, Channel backend) {
        byte[] bytes = ByteBufUtil.getBytes(buf);
        boolean enter = Arrays.equals(CMD, bytes)
                || Arrays.equals(SHELL, bytes)
                || Arrays.equals(X_SHELL, bytes);
        backend.attr(Constants.ATTR_STDOUT).set(enter);
        if (enter && backend.hasAttr(Constants.ATTR_HOST_NAME)) {
            //分包
            String hostName = backend.attr(Constants.ATTR_HOST_NAME).get();
            log.info("add limiters");
            ByteBuf[] limiters = new ByteBuf[]{
                    Unpooled.wrappedBuffer(new byte[]{0x0d, 0x0a}),
                    Unpooled.wrappedBuffer(new byte[]{0x0d, 0x00}),
                    Unpooled.wrappedBuffer(new byte[]{0x0a, 0x00}),
                    Unpooled.wrappedBuffer(new byte[]{0x0d}),
                    Unpooled.wrappedBuffer(new byte[]{0x0a}),
                    Unpooled.wrappedBuffer(new byte[]{0x2d, 0x2d, 0x2d, 0x2d, 0x20, 0x4d, 0x6f, 0x72, 0x65, 0x20, 0x2d, 0x2d, 0x2d, 0x2d}),
                    Unpooled.wrappedBuffer(BufferUtils.fromString(hostName)),
                    Unpooled.wrappedBuffer(new byte[]{0x1b, 0x5b, 0x31, 0x36, 0x44})
            };
            ChannelHandler delimiterHandler = new DelimiterBasedFrameDecoder(2048, false, limiters);
            backend.pipeline()
                    .addFirst(Constants.KEY_LIMITER, delimiterHandler);
        }
        return buf;
    }

    @Override
    public void onBackendPipeline(ChannelPipeline pipeline) {
    }

    @Override
    public void onFrontendPipeline(ChannelPipeline pipeline) {
    }

    @Override
    public ByteBuf onBackend(ByteBuf buf, Channel front, Channel backend) {
        String content = BufferUtils.toString(buf);
        //主机名
        Matcher matcher = HOST_REG.matcher(content);
        if (matcher.find()) {
            if (!backend.hasAttr(Constants.ATTR_HOST_NAME)) {
                String hostName = matcher.group(1);
                log.info("find host-name:{}", hostName);
                backend.attr(Constants.ATTR_HOST_NAME).set(hostName);
            } else {
                //返回主机名，代表输出结束
                backend.attr(Constants.ATTR_STDOUT).set(false);
                backend.attr(Constants.ATTR_STA_TAG).set(false);
                // 移除分包
                ChannelHandler handler = backend.pipeline().get(Constants.KEY_LIMITER);
                if (handler != null) {
                    log.info("remove limiters");
                    backend.pipeline().remove(handler);
                }
            }
        }
        return buf;
    }
}
