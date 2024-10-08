package com.portabull.generic.dao;

import com.portabull.response.PortableResponse;

import java.util.Map;

public interface GenericDao {

    PortableResponse saveClientContactDetails(Map<String, String> payload);

    PortableResponse getClientContactDetails();

    PortableResponse saveSchedularDetails(Map<String, Object> payload);
    PortableResponse getSchedulers();

    public PortableResponse changeSchedulerStatus(Long schedulerId, Boolean status);

    Map<String,Object> getSchedularDetails(String schedulerId);
}
