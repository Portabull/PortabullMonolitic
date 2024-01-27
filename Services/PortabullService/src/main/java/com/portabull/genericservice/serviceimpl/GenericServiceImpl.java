package com.portabull.genericservice.serviceimpl;

import com.portabull.constants.PortableConstants;
import com.portabull.execption.BadRequestException;
import com.portabull.generic.dao.GenericDao;
import com.portabull.genericservice.service.GenericService;
import com.portabull.response.PortableResponse;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.net.ssl.SSLContext;
import java.util.Arrays;
import java.util.Map;

@Service
public class GenericServiceImpl implements GenericService {


    static RestTemplate template;

    static {
        try {
            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(new TrustSelfSignedStrategy())
                    .build();

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLContext(sslContext)
                    .build();

            HttpComponentsClientHttpRequestFactory requestFactory =
                    new HttpComponentsClientHttpRequestFactory(httpClient);

            template = new RestTemplate(requestFactory);

            template.setErrorHandler(new DefaultResponseErrorHandler() {
                @Override
                protected boolean hasError(HttpStatus statusCode) {
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Autowired
    GenericDao genericDao;

    @Override
    public PortableResponse saveClientContactDetails(Map<String, String> payload) {
        return genericDao.saveClientContactDetails(payload);
    }

    @Override
    public PortableResponse getClientContactDetails() {
        return genericDao.getClientContactDetails();
    }

    @Override
    public PortableResponse callRestService(Map<String, Object> payload) {

        try {

            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(payload.get("url").toString());

            Map<String, String> headers = (Map<String, String>) payload.get("headers");

            Map<String, String> params = (Map<String, String>) payload.get("params");

            Object body = payload.get("body");

            HttpEntity entity;

            HttpHeaders httpHeaders = new HttpHeaders();

            if (!CollectionUtils.isEmpty(headers))
                headers.forEach((k, v) -> httpHeaders.put(k, Arrays.asList(v)));

            if (body != null && !StringUtils.isEmpty(body)) {
                entity = new HttpEntity<>(body, httpHeaders);
            } else {
                entity = new HttpEntity(headers);
            }

            if (!CollectionUtils.isEmpty(params))
                params.forEach((k, v) -> builder.queryParam(k, v));

            ResponseEntity<String> response = template.exchange(builder.build().toString(), getHttpMethod(payload.get("method").toString()), entity, String.class);

            return new PortableResponse("", Long.valueOf(response.getStatusCode().value()), PortableConstants.SUCCESS, response.getBody());

        } catch (ResourceAccessException e1) {
            if (e1.getMessage().contains("Connection refused: connect")) {
                return new PortableResponse("", 503L, PortableConstants.SUCCESS, "Connection refused");
            } else if (e1.getMessage().contains("No such host is known ")) {
                return new PortableResponse("", 503L, PortableConstants.SUCCESS, "No such host is known");
            }

            return new PortableResponse("", 503L, PortableConstants.SUCCESS, "Connection Issue");
        } catch (HttpClientErrorException ee) {
            return new PortableResponse("", 503L, PortableConstants.SUCCESS, "Connection Issue");
        } catch (Exception e) {
            return new PortableResponse("", 500L, PortableConstants.SUCCESS, "Internal Server Error");
        }
    }

    public HttpMethod getHttpMethod(String method) {
        switch (method) {
            case "POST":
                return HttpMethod.POST;
            case "GET":
                return HttpMethod.GET;
            case "PUT":
                return HttpMethod.PUT;
            case "DELETE":
                return HttpMethod.DELETE;
            default:
                throw new BadRequestException("Method Not Found");
        }

    }

}
