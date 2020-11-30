package com.yunzhicloud.proxy.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shay
 * @date 2020/11/30
 */
public class RegexUtils {

    public static String replace(String regex, CharSequence content, int group, CharSequence replacement) {
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(content);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            if (group > 0 && group > matcher.groupCount()) {
                continue;
            }
            sb.append(content.subSequence(0, matcher.start(group)));
            sb.append(replacement);
            sb.append(content.subSequence(matcher.end(group), content.length()));
        }
        return sb.toString();
    }
}
