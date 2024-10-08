package com.portabull.genericservice.serviceimpl;

import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.execption.BadRequestException;
import com.portabull.execption.UnAuthorizedException;
import com.portabull.generic.dao.CommonDao;
import com.portabull.generic.dao.GenericDao;
import com.portabull.generic.models.GenericCache;
import com.portabull.genericservice.service.GenericService;
import com.portabull.response.PortableResponse;
import com.portabull.um.UserCredentials;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.List;
import java.util.Map;

@Service
public class GenericServiceImpl implements GenericService {

    static RestTemplate template;

    private static final Logger logger = LoggerFactory.getLogger(GenericServiceImpl.class);

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

    @Autowired
    CommonDao commonDao;

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
                entity = new HttpEntity(httpHeaders);
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
            logger.error("Exception :: ", e);
            return new PortableResponse("", 500L, PortableConstants.SUCCESS, "Internal Server Error");
        }
    }

    @Override
    public PortableResponse saveSchedularDetails(Map<String, Object> payload) {

        return genericDao.saveSchedularDetails(payload);

    }

    @Override
    public PortableResponse saveCache(Map<String, Object> data) {

        UserCredentials userCredentials = commonDao.findEntity(UserCredentials.class,
                Long.valueOf(data.get("userId").toString()));

        if (userCredentials == null)
            throw new UnAuthorizedException(PortableConstants.AUTHENTICATION_FAILED);

        boolean isGet = data.containsKey("get") ? Boolean.valueOf(data.get("get").toString()) : false;

        String key = data.get("key").toString();

        List<GenericCache> genericCaches = commonDao.findEntitiesByCriteria(GenericCache.class, "key", key,
                "userId", userCredentials.getUserID());

        if (isGet) {
            if (!CollectionUtils.isEmpty(genericCaches)) {

                GenericCache genericCache = genericCaches.get(0);

                return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, genericCache.getValue());

            } else {

                return new PortableResponse("", StatusCodes.C_400, PortableConstants.SUCCESS, null);

            }
        }

        if (!CollectionUtils.isEmpty(genericCaches)) {

            boolean overwrite = data.containsKey("overwrite") ? Boolean.valueOf(data.get("overwrite").toString()) : false;

            if (overwrite) {

                GenericCache genericCache = genericCaches.get(0);

                genericCache.setKey(key);

                genericCache.setValue(data.get("value").toString());

                commonDao.saveOrUpdateEntity(genericCache);

            } else {
                return new PortableResponse("Key Exists, if you want to overwrite send the flag",
                        StatusCodes.C_200, PortableConstants.SUCCESS, null);
            }

        } else {

            GenericCache genericCache = new GenericCache();

            genericCache.setUserId(userCredentials.getUserID());

            genericCache.setKey(key);

            genericCache.setValue(data.get("value").toString());

            commonDao.saveOrUpdateEntity(genericCache);

        }

        return new PortableResponse("",
                StatusCodes.C_200, PortableConstants.SUCCESS, null);
    }

    @Override
    public PortableResponse getSchedulers() {
        return genericDao.getSchedulers();
    }

    @Override
    public PortableResponse changeSchedulerStatus(Long schedulerId, Boolean status) {
        return genericDao.changeSchedulerStatus(schedulerId, status);
    }

    @Override
    public Map<String, Object> getSchedularDetails(String schedular_id){
        return genericDao.getSchedularDetails(schedular_id);
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
