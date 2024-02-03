package com.portabull.generic.dao;

import com.portabull.response.PortableResponse;

import java.util.Map;

public interface GenericDao {

    PortableResponse saveClientContactDetails(Map<String, String> payload);

    PortableResponse getClientContactDetails();

    PortableResponse saveSchedularDetails(Map<String, Object> payload);
}
