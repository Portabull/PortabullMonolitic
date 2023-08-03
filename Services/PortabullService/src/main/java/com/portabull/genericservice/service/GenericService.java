package com.portabull.genericservice.service;

import com.portabull.response.PortableResponse;

import java.util.Map;

public interface GenericService {

    PortableResponse saveClientContactDetails(Map<String, String> payload);

    PortableResponse getClientContactDetails();

}
