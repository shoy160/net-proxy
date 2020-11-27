package com.yunzhicloud.proxy.filter;

import cn.hutool.core.io.BufferUtil;
import com.yunzhicloud.proxy.util.BufferUtils;
import com.yunzhicloud.proxy.util.SpringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author shay
 * @date 2020/11/27
 */
@Slf4j
public class TransferManager {
    private final Map<String, ProxyFilter> filters;

    public TransferManager() {
        filters = SpringUtils.getBeans(ProxyFilter.class);
    }

    public ByteBuf transferMsg(ByteBuf buf) {
        String content = BufferUtils.byteToString(buf);
        log.info("back proxy read {}", content);
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        for (ProxyFilter filter : filters.values()) {
            if (filter.isMatch(content)) {
                data = filter.transfer(data);
            }
        }
        ReferenceCountUtil.release(buf);
        return Unpooled.wrappedBuffer(data);
    }
}
