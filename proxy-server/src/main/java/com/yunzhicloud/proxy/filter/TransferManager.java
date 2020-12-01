package com.yunzhicloud.proxy.filter;

import com.yunzhicloud.proxy.config.Constants;
import com.yunzhicloud.proxy.util.BufferUtils;
import com.yunzhicloud.proxy.util.SpringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shay
 * @date 2020/11/27
 */
@Slf4j
public class TransferManager {
    private final Map<String, ProxyFilter> filters;
    private String ethName;

    private final static String ETH_REGEX = "((Ethernet[0-9/]+)|(Eth-Trunk[0-9/]+)|(Route-Aggregation[0-9/]+))\\s+current\\s+state\\s+:\\s+UP";

    public TransferManager() {
        filters = SpringUtils.getBeans(ProxyFilter.class);
    }

    public ByteBuf transferMsg(ByteBuf buf, Channel channel) {
        String content = BufferUtils.byteToString(buf);
        Matcher matcher = Pattern.compile(ETH_REGEX).matcher(content);
        if (matcher.find()) {
            ethName = matcher.group(1);
        }
        log.info("begin transfer: {}", content);
        for (ProxyFilter filter : filters.values()) {
            if (filter.isMatch(content, ethName, 0)) {
                content = filter.transfer();
            }
        }
        ReferenceCountUtil.release(buf);
        return Unpooled.wrappedBuffer(BufferUtils.stringToBytes(content));
    }
}
