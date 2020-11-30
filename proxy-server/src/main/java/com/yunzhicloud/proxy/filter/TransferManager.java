package com.yunzhicloud.proxy.filter;

import com.yunzhicloud.proxy.util.BufferUtils;
import com.yunzhicloud.proxy.util.SpringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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
    private String prefixContent = "";
    private final static String ETH_REGEX = "((Ethernet[0-9/]+)|(Eth-Trunk[0-9/]+)|(Route-Aggregation[0-9/]+))\\s+current\\s+state\\s+:\\s+UP";

    public TransferManager() {
        filters = SpringUtils.getBeans(ProxyFilter.class);
    }

    public ByteBuf transferMsg(ByteBuf buf) {
        String content = BufferUtils.byteToString(buf);
        Matcher matcher = Pattern.compile(ETH_REGEX).matcher(content);
        if (matcher.find()) {
            ethName = matcher.group(1);
        }
        //缓存上一段响应信息，减少出错率
        content = prefixContent.concat(content);
        log.debug("begin transfer: {}", content);
        for (ProxyFilter filter : filters.values()) {
            if (filter.isMatch(content, ethName, prefixContent.length() + 1)) {
                content = filter.transfer();
            }
        }
        content = content.substring(prefixContent.length());
        prefixContent = content;
        ReferenceCountUtil.release(buf);
        return Unpooled.wrappedBuffer(BufferUtils.stringToBytes(content));
    }
}
