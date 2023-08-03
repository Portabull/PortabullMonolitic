package com.portabull.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.portabull.constants.PortableConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class GenericResponse {

    private GenericResponse() {

    }

    static Logger logger = LoggerFactory.getLogger(GenericResponse.class);


    static final ObjectMapper objectMapper = new ObjectMapper();


    public static ObjectNode prepareResponseObjectNode(Integer statusCode, String message, String status, String error,
                                                       String path, Object data) {
        ObjectNode response = GenericResponse.createObjectNode();
        response.put(PortableConstants.TIMESTAMP, new Date().getTime());
        response.put(PortableConstants.STATUS, status);
        response.put(PortableConstants.ERROR, error);
        response.put(PortableConstants.MESSAGE, message);
        response.put(PortableConstants.PATH, path);
        response.put(PortableConstants.STATUS_CODE, statusCode);
        response.put(PortableConstants.DATA,
                data instanceof String ? (String) data : GenericResponse.getObjectAsJsonString(data));
        return response;

    }

    public static String getObjectAsJsonString(Object tokenResponse) {
        try {
            return objectMapper.writeValueAsString(tokenResponse);
        } catch (JsonProcessingException e) {
            logger.error("while processing json it throws error", e);
        }
        return "";
    }

    public static ObjectNode createObjectNode() {
        return objectMapper.createObjectNode();
    }
}
