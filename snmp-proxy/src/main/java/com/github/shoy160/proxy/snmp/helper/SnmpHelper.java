package com.github.shoy160.proxy.snmp.helper;

import cn.hutool.core.util.StrUtil;
import com.github.shoy160.proxy.util.BufferUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shay
 * @date 2020/12/11
 */
public class SnmpHelper {

    /**
     * ObjectId 解码
     *
     * @param data byte[]
     * @return oid
     */
    public static String decodeObjectId(byte[] data) {
        // https://blog.csdn.net/smartfox80/article/details/18899707
        StringBuilder sb = new StringBuilder();
        int tag = data[0];
        sb.append(String.format("%d.%d", tag / 40, tag % 40));
        StringBuilder binaryString = new StringBuilder();
        for (int i = 1; i < data.length; i++) {
            String binary = Integer.toBinaryString((data[i] & 0xFF) + 0x100).substring(1);
            binaryString.append(binary.substring(1));
            char high = binary.charAt(0);
            if (high == '0') {
                sb.append(String.format(".%d", Integer.parseInt(binaryString.toString(), 2)));
                binaryString.setLength(0);
            }
        }
        return sb.toString();
    }

    /**
     * ObjectId 编码
     *
     * @param oid oid
     * @return byte[]
     */
    public static byte[] encodeObjectId(String oid) {
        // https://blog.csdn.net/smartfox80/article/details/18899707
        String[] array = oid.split("\\.");
        int i;
        final int startIndex = 2;
        final int perLength = 7;
        ArrayList<Byte> dataList = new ArrayList<>();
        //首位
        dataList.add((byte) (Byte.parseByte(array[0]) * 40 + Byte.parseByte(array[1])));

        for (i = startIndex; i < array.length; i++) {
            int value = Integer.parseInt(array[i]);
            String binary = Integer.toBinaryString(value);
            int len = binary.length(), index = (int) Math.floor(len / (float) perLength), start, end;
            while (true) {
                start = len - (index + 1) * perLength;
                end = len - index * perLength;
                // 防止溢出
                if (start < 0) {
                    start = 0;
                }
                if (end > len) {
                    end = len;
                }
                String item = binary.substring(start, end);
                item = StrUtil.padPre(item, perLength, '0');
                // 超过127，高位为1，最后一位高位为0
                char high = (index == 0 ? '0' : '1');
                String itemBinary = String.format("%c%s", high, item);
                dataList.add((byte) Integer.parseInt(itemBinary, 2));
                if (index == 0) {
                    break;
                }
                index--;
            }
        }
        return array(dataList);
    }

    public static byte[] array(List<Byte> byteList) {
        byte[] data = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            data[i] = byteList.get(i);
        }
        return data;
    }
}
