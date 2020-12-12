package com.yunzhicloud.proxy.filter;

import com.github.shoy160.proxy.util.BufferUtils;
import com.github.shoy160.proxy.util.RegexUtils;
import com.yunzhicloud.proxy.AcConstants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shay
 * @date 2020/12/12
 */
@Slf4j
@Component
public class AcFilter {
    private static final String TOTAL_REG = "(?:\\s+Total\\s+access\\s+number|\\s*Total\\s+number\\s+of\\s+online\\s+clients|\\s*Total\\s+Sta\\s+Num)\\s*:\\s*([0-9]+)";
    private static final Pattern TOTAL_PATTERN = Pattern.compile(TOTAL_REG, Pattern.DOTALL);

    private static final Pattern STA_START_REGEX = Pattern.compile("Total\\s+AP\\s+information:", Pattern.DOTALL);
    private static final Pattern STA_REGEX = Pattern.compile("(?:[^\\s]+\\s+){7}([^\\s]+\\s+)[^\\s]+\\s+[^\\s]+\\s*", Pattern.DOTALL);

    public ByteBuf transform(ByteBuf buf, Channel channel) {
        String content = BufferUtils.toString(buf);
        if (STA_START_REGEX.matcher(content).find()) {
            channel.attr(AcConstants.ATTR_STA_TAG).set(true);
            return buf;
        }
        Matcher matcher = TOTAL_PATTERN.matcher(content);
        if (matcher.find()) {
            //总数
            content = RegexUtils.replace(TOTAL_PATTERN, content, 1, "120000");
            ReferenceCountUtil.release(buf);
            return Unpooled.wrappedBuffer(BufferUtils.fromString(content));
        }
        if (channel.hasAttr(AcConstants.ATTR_STA_TAG) && channel.attr(AcConstants.ATTR_STA_TAG).get()) {
            //STA
            matcher = STA_REGEX.matcher(content);
            if (matcher.find()) {
                content = RegexUtils.replace(STA_REGEX, content, 1, "");
                ReferenceCountUtil.release(buf);
                return Unpooled.wrappedBuffer(BufferUtils.fromString(content));
            }
        }
        return buf;
    }
}
