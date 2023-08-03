package com.portabull.utils;

import java.util.Arrays;
import java.util.Comparator;

public class Calculate {

    private Calculate() {

    }

    public static double percentAmount(double percent, double amount) {
        return (amount * percent) / 100;
    }

    public static double getMinValue(double value1, double value2) {
        return value1 > value2 ? value2 : value1;
    }

    public static double getMaxValue(double value1, double value2) {
        return value2 > value1 ? value2 : value1;
    }

    public static Integer getMaxValue(Integer... values) {
        return Arrays.stream(values).max(Comparator.comparing(Integer::valueOf))
                .get();
    }

    public static Integer getMinValue(Integer... values) {
        return Arrays.stream(values).min(Comparator.comparing(Integer::valueOf))
                .get();
    }

    public static Long getMaxValue(Long... values) {
        return Arrays.stream(values).max(Comparator.comparing(Long::valueOf))
                .get();
    }

    public static Long getMinValue(Long... values) {
        return Arrays.stream(values).min(Comparator.comparing(Long::valueOf))
                .get();
    }

}
