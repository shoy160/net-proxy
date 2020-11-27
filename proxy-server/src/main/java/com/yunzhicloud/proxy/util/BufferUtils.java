package com.yunzhicloud.proxy.util;

import cn.hutool.core.util.HexUtil;
import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

/**
 * @author shay
 * @date 2020/11/27
 */
public class BufferUtils {

    private static final char[] BYTE2CHAR = new char[256];

    private static volatile boolean initTag = false;

    private static synchronized void initChars() {
        if (initTag) {
            return;
        }
        for (int i = 0; i < BYTE2CHAR.length; ++i) {
            BYTE2CHAR[i] = (char) i;
        }
        initTag = true;
    }

    public static String toHex(byte[] bytes) {
        return HexUtil.encodeHexStr(bytes);
    }

    public static String byteToString(ByteBuf buf) {
        initChars();
        int length = buf.readableBytes();
        StringBuilder content = new StringBuilder();
        int i;

        for (i = buf.readerIndex(); i < length; i++) {
            content.append(BYTE2CHAR[buf.getUnsignedByte(i)]);
        }
        return content.toString();
    }

    public static byte[] stringToBytes(String msg) {
        byte[] bytes = new byte[msg.length()];
        final char[] chars = msg.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            bytes[i] = (byte) chars[i];
        }
        return bytes;
    }

    public static byte[] combineBytes(byte[] data, String other) {
        return combineBytes(data, stringToBytes(other));
    }

    public static byte[] combineBytes(byte[] data, byte[] other) {
        ByteBuffer buffer = ByteBuffer.allocate(data.length + other.length);
        buffer.put(data);
        buffer.put(other);
        return buffer.array();
    }

    public static void replaceBytes(byte[] bytes, String content, int start) {
        byte[] sources = stringToBytes(content);
        replaceBytes(bytes, sources, start, -1);
    }

    public static void replaceBytes(byte[] bytes, byte[] sources, int start) {
        replaceBytes(bytes, sources, start, -1);
    }

    public static void replaceBytes(byte[] bytes, byte[] sources, int start, int length) {
        if (bytes.length <= start) {
            return;
        }
        if (start < 0) {
            start = 0;
        }
        if (length <= 0) {
            length = sources.length;
        }
        System.arraycopy(sources, 0, bytes, start, length);
    }
}
