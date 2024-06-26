package com.portabull.genericservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.portabull.response.PortableResponse;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public interface ExternalJobService {

    public PortableResponse sendEmail(Map<String, Object> mailPayload) throws JsonProcessingException;

    public PortableResponse executeRestAPI(List<Map<String, Object>> restPayloads) throws JsonProcessingException;

    public PortableResponse executeCode(Map<String, String> codePayload) throws Exception;

    Object tempEndpointKeka(String refreshToken, String flag) throws IOException;
}
