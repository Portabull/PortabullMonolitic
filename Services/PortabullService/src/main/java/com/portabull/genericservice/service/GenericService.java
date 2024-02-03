package com.portabull.genericservice.service;

import com.portabull.response.PortableResponse;

import java.util.Map;

public interface GenericService {

    PortableResponse saveClientContactDetails(Map<String, String> payload);

    PortableResponse getClientContactDetails();

    PortableResponse callRestService(Map<String, Object> payload);

    PortableResponse saveSchedularDetails(Map<String, Object> payload);
}
