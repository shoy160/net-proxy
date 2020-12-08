package com.yunzhicloud.proxy.filter;

import com.github.shoy160.proxy.util.BufferUtils;
import com.yunzhicloud.proxy.config.TransferConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shay
 * @date 2020/11/27
 */
@Slf4j
@Component
@Scope("prototype")
public class TransferManager {
    private final Map<String, ProxyFilter> filters;
    private String ethName;
    private final Pattern ETH_REGEX;

    public TransferManager(TransferConfig config, Map<String, ProxyFilter> filters) {
        this.filters = filters;
        ETH_REGEX = Pattern.compile(config.getInterfaceRegex().concat("\\s+current\\s+state\\s+:\\s+UP"), Pattern.DOTALL);
    }

    public ByteBuf transferMsg(ByteBuf buf) {
        String content = BufferUtils.toString(buf);
        Matcher matcher = ETH_REGEX.matcher(content);
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
        return Unpooled.wrappedBuffer(BufferUtils.fromString(content));
    }
}
