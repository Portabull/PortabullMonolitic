package com.portabull.genericservice.service;

import com.portabull.response.PortableResponse;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public interface GenericService {

    PortableResponse saveClientContactDetails(Map<String, String> payload);

    PortableResponse getClientContactDetails();

    PortableResponse callRestService(Map<String, Object> payload) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException;
}
