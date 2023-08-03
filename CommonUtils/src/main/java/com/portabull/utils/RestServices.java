package com.portabull.utils;

import org.springframework.http.*;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

public class RestServices {

    private RestServices() {

    }

    public static <T> ResponseEntity<T> executeRestService(String url, HttpMethod method,
                                                           Map<String, String> requestHeaders,
                                                           Map<String, Object> requestParams,
                                                           T requestBody, Class<T> classType) {

        url = buildRequestParamsForUrl(url, requestParams);

        HttpHeaders headers = buildHeaders(requestHeaders);

        HttpEntity<T> httpEntity = buildHttpEntity(requestBody, headers);

        return executeRequest(url, method, httpEntity, classType);

    }

    public static String buildRequestParamsForUrl(String url, Map<String, Object> requestParams) {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(url);
        if (!CollectionUtils.isEmpty(requestParams))
            requestParams.forEach(urlBuilder::queryParam);

        return urlBuilder.toUriString();
    }


    public static HttpHeaders buildHeaders(Map<String, String> requestHeaders) {
        HttpHeaders headers = new HttpHeaders();
        if (CollectionUtils.isEmpty(requestHeaders))
            return headers;

        requestHeaders.forEach(headers::set);
        return headers;
    }

    private static <T> HttpEntity<T> buildHttpEntity(T requestBody, HttpHeaders headers) {
        HttpEntity<T> httpEntity;
        if (requestBody != null)
            httpEntity = new HttpEntity(requestBody, headers);
        else
            httpEntity = new HttpEntity<>(headers);
        return httpEntity;
    }

    private static <T> ResponseEntity<T> executeRequest(String url, HttpMethod method, HttpEntity<T> httpEntity, Class<T> classType) {
        ResponseEntity<T> responseEntity = null;
        RestTemplate template = createRestTemplate();
        try {
            if (method.equals(HttpMethod.POST)) {
                responseEntity = template.postForEntity(url, httpEntity, classType);
            } else if (method.equals(HttpMethod.GET)) {
                responseEntity = template.getForEntity(url, classType);
            } else if (method.equals(HttpMethod.DELETE)) {
                template.delete(url);
                responseEntity = new ResponseEntity("SUCCESS", HttpStatus.OK);
            } else {
                responseEntity = template.exchange(url, method, httpEntity, classType);
            }
        } catch (Exception e) {
            throw new RestClientException("Api Failed");
        }
        return responseEntity;
    }

    public static RestTemplate createRestTemplate() {
        return new RestTemplate();
    }


}
