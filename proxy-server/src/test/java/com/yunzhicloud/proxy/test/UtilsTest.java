package com.yunzhicloud.proxy.test;

import com.yunzhicloud.proxy.util.CommonUtils;
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
        byte[] bytes = CommonUtils.stringToBytes("52.1%");
        log.info(CommonUtils.toHex(bytes));
    }

    @Test
    public void matchTest() {
        String src = "..Ethernet0/0/0               up    up        0.01%  0.01%          0          0,80,0";
        boolean match = src.matches("^\\.\\.Ethernet0/0/0.*$");
        log.info("is match {}", match);
    }
}
