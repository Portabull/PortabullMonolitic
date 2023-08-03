package com.portabull.utils.validationutils;

import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Validations {

    private Validations() {

    }

    public static final String OS = "os.name";

    public static String getOsName() {
        return System.getProperty(OS);
    }

    public static boolean isStringEmpty(Object str) {
        return (str == null || "".equals(str.toString().trim()));
    }

    public static boolean removeEmptyValues(List<String> list) {
        if (!CollectionUtils.isEmpty(list)) {
            return list.removeAll(Arrays.asList("", null));
        }
        return false;
    }

    public static boolean removeNullValues(Map<?, ?> map) {
        if (CollectionUtils.isEmpty(map))
            return false;
        return map.values().removeAll(Collections.singleton(null));
    }

    public static String append(String... strings) {
        StringBuilder builder = new StringBuilder();
        for (String string : strings) {
            builder.append(string);
        }
        return builder.toString();
    }

    public static boolean isEmptyObject(Object object) {
        return object == null;
    }


}
