package com.portabull.utils.annotation;

public final class ServicePayload {

    private final String name;

    private final Object bean;

    public ServicePayload(String name, Object bean) {
        this.name = name;
        this.bean = bean;
    }

    public String getName() {
        return name;
    }

    public Object getBean() {
        return bean;
    }
}