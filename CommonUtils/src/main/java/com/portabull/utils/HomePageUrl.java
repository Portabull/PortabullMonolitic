package com.portabull.utils;

import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import com.portabull.execption.MultipartException;
import com.portabull.payloads.KeyValueMapping;
import com.portabull.payloads.MultiPartFileRequest;
import com.portabull.payloads.MultiPartRequest;
import net.vidageek.mirror.dsl.Mirror;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

@Component
public class HomePageUrl {

    String server = "server";

    @Autowired
    private LoadBalancerClient loadBalancerClient;


    public String getHomePageUrl(String serviceId) {
        try {
            if (serviceId != null) {
                return new StringBuilder(((DiscoveryEnabledServer) new Mirror()
                        .on(loadBalancerClient.choose(serviceId)).get().field(server)).getInstanceInfo().getHomePageUrl()).append("/").toString();
            }
        } catch (IllegalArgumentException e) {
            throw new RestClientException(serviceId + " Server is Down Please try after some time");
        }
        return null;
    }

    public <T> ResponseEntity<T> invokeRestCall(String serviceID, String endPoint, HttpMethod requestMethod,
                                                Map<String, String> requestHeaders, Map<String, Object> requestParams
            , Object requestBody, Class<T> classType) {

        return RestServices.executeRestService(
                prepareServiceUrl(
                        serviceID, endPoint
                ), requestMethod, requestHeaders,
                requestParams, (T) requestBody, classType
        );

    }

    public <T> ResponseEntity<T> invokeRestCall(String serviceID, String endPoint, HttpMethod requestMethod,
                                                Map<String, String> requestHeaders, Map<String, Object> requestParams,
                                                MultiPartFileRequest multipartFileRequest, Class<T> classType) throws IOException {

        return RestServices.executeRestService(
                prepareServiceUrl(
                        serviceID, endPoint
                ), requestMethod, requestHeaders,
                requestParams, (T) prepareMultipartRequest(multipartFileRequest), classType
        );

    }

    public MultiValueMap<String, Object> prepareMultipartRequest(MultiPartFileRequest multipartRequest) throws IOException {
        if (multipartRequest == null || CollectionUtils.isEmpty(multipartRequest.getMultiPartRequests()))
            throw new MultipartException("Multipart Request Not Found");


        KeyValueMapping<String, Object> keyValueMapping = new KeyValueMapping<>();
        for (MultiPartRequest multiPartRequest : multipartRequest.getMultiPartRequests()) {
            keyValueMapping.setKeys(multiPartRequest.getParameterName())
                    .setValues(new HttpEntity<>(getMultiPartBytes(multiPartRequest),
                            prepareFileHeaders(multiPartRequest.getParameterName(),
                                    multiPartRequest.getFileNameWithExtension().toLowerCase(),
                                    multiPartRequest.getContentType(), multiPartRequest.getType())));
        }
        return keyValueMapping.getResultAsMultiValueMap();
    }

    private byte[] getMultiPartBytes(MultiPartRequest multipartRequest) throws IOException {
        if (multipartRequest.getBytes() != null && multipartRequest.getBytes().length > 0) {
            return multipartRequest.getBytes();
        } else if (multipartRequest.getFile() != null) {
            return Files.readAllBytes(multipartRequest.getFile().toPath());
        } else if (multipartRequest.getInputStream() != null) {
            return IOUtils.toByteArray(multipartRequest.getInputStream());
        }
        throw new MultipartException("File content should not be null");
    }

    private HttpHeaders prepareFileHeaders(String fileKey, String fileName, String contentType, String type) {
        HttpHeaders fileHeaders = new HttpHeaders();
        fileHeaders.set(HttpHeaders.CONTENT_TYPE, contentType);
        fileHeaders.set(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.builder(type)
                .name(fileKey).filename(fileName).build().toString());
        return fileHeaders;
    }

    public String prepareServiceUrl(String serviceID, String endPoint) {
        return new StringBuilder().append(getHomePageUrl(serviceID)).append(endPoint).toString();
    }

}
