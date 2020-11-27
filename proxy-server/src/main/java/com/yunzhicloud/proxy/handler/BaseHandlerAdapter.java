package com.yunzhicloud.proxy.handler;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;

/**
 * @author shay
 * @date 2020/11/27
 */
public class BaseHandlerAdapter extends ChannelInboundHandlerAdapter {
    protected final static AttributeKey<String> COMMAND_ATTR = AttributeKey.valueOf("command_attr");
}
