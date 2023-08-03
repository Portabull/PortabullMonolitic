package com.portabull.utils;

public class BeanUtils {

    private BeanUtils() {
    }


    public static <T> T createBean(Class<T> clazz) throws IllegalAccessException, InstantiationException {
        return clazz.newInstance();
    }

    public static <T> T createBeanWithClassName(String className) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException {
        return (T) createBean(Class.forName(className));
    }


}
