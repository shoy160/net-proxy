package com.github.shoy160.proxy.util;

import cn.hutool.core.util.HexUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * @author shay
 * @date 2020/12/2
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

    public static String toHex(ByteBuf buf) {
        byte[] bytes = new byte[buf.readableBytes()];
        buf.getBytes(buf.readerIndex(), bytes);
        return HexUtil.encodeHexStr(bytes);
    }

    public static String toHex(byte[] bytes) {
        return HexUtil.encodeHexStr(bytes);
    }

    public static String toString(ByteBuf buf) {
        initChars();
        int length = buf.readableBytes();
        StringBuilder content = new StringBuilder();
        int i;

        for (i = buf.readerIndex(); i < length; i++) {
            content.append(BYTE2CHAR[buf.getUnsignedByte(i)]);
        }
        return content.toString();
    }

    public static String toString(byte[] data) {
        initChars();
        StringBuilder content = new StringBuilder();
        for (byte datum : data) {
            content.append(BYTE2CHAR[datum]);
        }
        return content.toString();
    }

    public static byte[] fromString(String msg) {
        byte[] bytes = new byte[msg.length()];
        final char[] chars = msg.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            bytes[i] = (byte) chars[i];
        }
        return bytes;
    }

    public static byte[] combine(byte[] data, String other) {
        return combine(data, fromString(other));
    }

    public static byte[] combine(byte[] data, byte[] other) {
        ByteBuffer buffer = ByteBuffer.allocate(data.length + other.length);
        buffer.put(data);
        buffer.put(other);
        return buffer.array();
    }

    public static void replace(byte[] bytes, String content, int start) {
        byte[] sources = fromString(content);
        replace(bytes, sources, start, -1);
    }

    public static void replace(byte[] bytes, byte[] sources, int start) {
        replace(bytes, sources, start, -1);
    }

    public static void replace(byte[] bytes, byte[] sources, int start, int length) {
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

    public static int toInt(byte[] bytes) {
        int value = 0;
        int length = bytes.length;
        if (length > 4) {
            //防止溢出
            length = 4;
        }
        for (int i = length - 1; i >= 0; i--) {
            value += (bytes[i] & 0xFF) << (length - i - 1) * 8;
        }
        return value;
    }

    public static long toLong(byte[] bytes) {
        return new BigInteger(bytes).longValue();
    }

    public static String toString(byte[] bytes, Charset charset) {
        if (charset == null) {
            charset = Charset.defaultCharset();
        }
        return new String(bytes, charset);
    }

    private static byte[] simple(byte[] bytes) {
        ByteBuf buf = Unpooled.wrappedBuffer(bytes);
        for (byte aByte : bytes) {
            if (aByte == 0) {
                buf.readByte();
            } else {
                break;
            }
        }
        return ByteBufUtil.getBytes(buf);
    }

    public static byte[] fromInt(int value) {
        byte[] result = new byte[4];
        for (int i = 3; i >= 0; i--) {
            result[i] = (byte) (value & 0xFF);
            value >>= 8;
        }
        return simple(result);
    }

    public static byte[] fromLong(long value) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (value & 0xFF);
            value >>= 8;
        }
        return simple(result);
    }
}
