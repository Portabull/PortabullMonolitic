package com.portabull.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

public class JsonUtils {

    private JsonUtils() {
    }


    static ObjectMapper objectMapper;

    static GsonBuilder gsonBuilder;

    static {
        objectMapper = new ObjectMapper();
        gsonBuilder = new GsonBuilder();
    }

    public static String getJsonString(Object data) throws JsonProcessingException {
        return objectMapper.writeValueAsString(data);
    }

    public static String beautiyJson(Object data) throws JsonProcessingException {
        return gsonBuilder.setPrettyPrinting().create().toJson(JsonParser.parseString(JsonUtils.getJsonString(data)));
    }

    public static <T> T processData(String data, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(data, clazz);
    }

}
