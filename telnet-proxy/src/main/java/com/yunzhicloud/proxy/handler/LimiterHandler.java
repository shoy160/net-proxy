package com.yunzhicloud.proxy.handler;

import cn.hutool.core.util.StrUtil;
import com.github.shoy160.proxy.Constants;
import com.github.shoy160.proxy.util.BufferUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.util.Attribute;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shay
 * @date 2020/12/1
 */
@Slf4j
public class LimiterHandler extends ChannelInboundHandlerAdapter {

    private final static Pattern HOST_REG = Pattern.compile("\r\n(<[^>]+>)", Pattern.DOTALL);
    private final static String MORE_STR = "---- More ----";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            Channel channel = ctx.channel();
            Attribute<String> hostAttr = channel.attr(Constants.ATTR_HOST_NAME);
            String hostName = hostAttr.get();
            String content = BufferUtils.toString((ByteBuf) msg);
            if (StrUtil.isEmpty(hostName)) {
                Matcher matcher = HOST_REG.matcher(content);
                if (matcher.find()) {
                    hostName = matcher.group(1);
                    log.info("find host-name:{}", hostName);
                    hostAttr.set(hostName);
                }
            } else {
                //host-name结束
                ChannelHandler handler = channel.pipeline().get(Constants.KEY_LIMITER);
                if (content.endsWith(hostName)) {
                    if (handler != null) {
                        log.info("remove limiters");
                        channel.pipeline().remove(handler);
                    }
                    channel.attr(Constants.ATTR_STDOUT).set(false);
                }
                if (channel.attr(Constants.ATTR_STDOUT).get() && handler == null) {
                    //开始按行处理
                    log.info("add limiters");
                    ByteBuf[] limiters = new ByteBuf[]{
                            Unpooled.wrappedBuffer(new byte[]{13, 10}),
                            Unpooled.wrappedBuffer(new byte[]{10}),
                            Unpooled.wrappedBuffer(BufferUtils.fromString(MORE_STR)),
                            Unpooled.wrappedBuffer(BufferUtils.fromString(hostName))
                    };
                    channel.pipeline().addFirst(Constants.KEY_LIMITER, new DelimiterBasedFrameDecoder(2048, false, limiters));
                }
            }
        }
        ctx.fireChannelRead(msg);
    }
}
