package com.portabull.utils.datautils;

import org.apache.commons.lang.ArrayUtils;

public class StringUtils {

    private StringUtils() {

    }

    public static String append(String... strings) {
        if (ArrayUtils.isEmpty(strings))
            return "";

        if (strings.length == 1) {
            return strings[0];
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String string : strings) {
            stringBuilder.append(string);
        }

        return stringBuilder.toString();
    }

    public static String append(Object... strings) {
        if (ArrayUtils.isEmpty(strings))
            return "";

        if (strings.length == 1) {
            return String.valueOf(strings[0]);
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (Object string : strings) {
            stringBuilder.append(string);
        }

        return stringBuilder.toString();
    }

}
