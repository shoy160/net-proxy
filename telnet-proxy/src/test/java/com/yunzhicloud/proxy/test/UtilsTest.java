package com.yunzhicloud.proxy.test;

import com.github.shoy160.proxy.util.BufferUtils;
import com.github.shoy160.proxy.util.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author shay
 * @date 2020/11/27
 */
@Slf4j
public class UtilsTest {

    @Test
    public void toBytesTest() {
        byte[] bytes = BufferUtils.fromString("52.1%");
        log.info(BufferUtils.toHex(bytes));
    }

    @Test
    public void matchTest() {
        String src = "\r\n   Ethernet0/0/0               up    up        0.01%  0.01%          0          0,80,0";
        final String regex = "Ethernet0/0/0\\s+up\\s+up\\s+([^\\s]+\\s+[^\\s]+)";
        src = RegexUtils.replace(regex, src, 1, "50.2%  45.6%");
        log.info("src {}", src);
    }
}
