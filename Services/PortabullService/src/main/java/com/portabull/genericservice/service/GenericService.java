package com.portabull.genericservice.service;
import java.util.Map;
import com.portabull.response.PortableResponse;

import java.util.Map;

public interface GenericService {

    PortableResponse saveClientContactDetails(Map<String, String> payload);

    PortableResponse getClientContactDetails();

    PortableResponse callRestService(Map<String, Object> payload);

    PortableResponse saveSchedularDetails(Map<String, Object> payload);

    PortableResponse saveCache(Map<String, Object> data);

    PortableResponse getSchedulers();

    public PortableResponse changeSchedulerStatus(Long schedulerId, Boolean status);

    Map<String, Object> getSchedularDetails(String schedular_id);
}
