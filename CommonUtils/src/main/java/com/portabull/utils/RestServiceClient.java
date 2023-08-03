package com.portabull.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.portabull.execption.InvalidMethodException;
import com.squareup.okhttp.*;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class RestServiceClient {

    static Logger logger = LoggerFactory.getLogger(RestServiceClient.class);

    private RestServiceClient() {
    }


    private static final String MEDIA_TYPE = "mediaType";

    private static final String DATA = "data";

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    private static final XmlMapper xmlMapper = new XmlMapper();

    public static <T> ResponseEntity<T> execute(String url, HttpMethod httpMethod,
                                                Class<T> clazz, Map<String, String> headers,
                                                Object requestBody, Map<String, String> requestParams) throws IOException, InvalidMethodException {


        if (isCustomizedRequest(httpMethod, requestBody)) {
            return executeRequest(url, httpMethod, clazz, headers, requestBody, requestParams);
        }

        T response;
        AtomicReference<Integer> statusCode = new AtomicReference<>(0);
        try (CloseableHttpClient client = HttpClients.createDefault()) {

            url = loadRequestParams(url, requestParams);

            HttpRequestBase request = loadRequest(url, httpMethod, requestBody);

            prepareHeaders(request, headers);

            response = client.execute(request, httpResponse ->
                    handleResponse(httpResponse, statusCode, clazz));
        }

        return new ResponseEntity<>(response, getHttpStatus(statusCode.get()));

    }

    private static <T> ResponseEntity<T> executeRequest(String url, HttpMethod httpMethod, Class<T> clazz, Map<String, String> headers, Object requestBody, Map<String, String> requestParams) throws IOException {
        T handledResponse;
        Map<String, String> requestData = executeRequestBody(requestBody);


        OkHttpClient client = new OkHttpClient();

        final MediaType mediaType
                = MediaType.parse(requestData.get(MEDIA_TYPE));

        url = loadRequestParams(url, requestParams);

        Request.Builder builder = new Request.Builder();
        Request request = null;
        if (!CollectionUtils.isEmpty(headers)) {
            headers.forEach(builder::header);
        }
        if (HttpMethod.DELETE.equals(httpMethod)) {
            request = builder.url(url).delete(RequestBody.create(mediaType, requestData.get(DATA)))
                    .build();
        }

        Response response = client.newCall(request).execute();

        handledResponse = handleResponse(response, clazz);

        return new ResponseEntity<>(handledResponse, getHttpStatus(response.code()));
    }

    private static boolean isCustomizedRequest(HttpMethod httpMethod, Object requestBody) {
        if (requestBody == null)
            return false;

        if (HttpMethod.DELETE.equals(httpMethod)) {
            return true;
        }

        return false;
    }

    private static String loadRequestParams(String url, Map<String, String> requestParams) {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(url);
        if (!CollectionUtils.isEmpty(requestParams))
            requestParams.forEach(urlBuilder::queryParam);

        return urlBuilder.toUriString();
    }

    private static void prepareHeaders(HttpRequestBase request, Map<String, String> headers) {
        if (CollectionUtils.isEmpty(headers))
            return;

        headers.forEach(request::setHeader);
    }

    private static HttpRequestBase loadRequest(String url, HttpMethod httpMethod, Object requestBody) throws InvalidMethodException, JsonProcessingException {
        switch (httpMethod) {
            case GET:
                return new HttpGet(url);
            case HEAD:
                return new HttpHead(url);
            case DELETE:
                return new HttpDelete(url);
            case OPTIONS:
                return new HttpOptions(url);
            case TRACE:
                return new HttpTrace(url);
            default:
                return prepareDefaultRequest(url, requestBody, httpMethod);
        }
    }

    private static HttpRequestBase prepareDefaultRequest(String url, Object requestBody, HttpMethod httpMethod) throws InvalidMethodException, JsonProcessingException {
        HttpEntityEnclosingRequestBase request;

        switch (httpMethod) {
            case PUT:
                request = new HttpPut(url);
                break;
            case POST:
                request = new HttpPost(url);
                break;
            case PATCH:
                request = new HttpPatch(url);
                break;
            default:
                throw new InvalidMethodException("Invalid Method");
        }

        if (requestBody != null) {
            Map<String, String> requestData = executeRequestBody(requestBody);
            request.setEntity(new StringEntity(requestData.get(DATA), ContentType.create(requestData.get(MEDIA_TYPE))));
        }
        return request;
    }

    public static Map<String, String> executeRequestBody(Object requestBody) throws JsonProcessingException {

        Map<String, String> data = new LinkedHashMap<>();
        if (requestBody != null) {
            if (requestBody instanceof String) {
                data.put(MEDIA_TYPE, "");
                data.put(DATA, (String) requestBody);
                return data;
            } else {
                try {
                    data.put(MEDIA_TYPE, "application/json");
                    data.put(DATA, jsonMapper.writeValueAsString(requestBody));
                    return data;
                } catch (JsonProcessingException e) {
                    data.put(MEDIA_TYPE, "application/xml; charset=utf-8");
                    data.put(DATA, xmlMapper.writeValueAsString(requestBody));
                    return data;
                }
            }
        }

        return data;
    }

    private static <T> T handleResponse(HttpResponse httpResponse, AtomicReference<Integer> statusCode, Class<T> clazz) throws IOException {
        T data;
        statusCode.set(httpResponse.getStatusLine().getStatusCode());
        byte[] bytes = IOUtils.toByteArray(httpResponse.getEntity().getContent());
        InputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        if (clazz.equals(String.class)) {
            return (T) IOUtils.toString(byteArrayInputStream, StandardCharsets.UTF_8);
        }
        try {
            data = jsonMapper.readValue(byteArrayInputStream, clazz);
        } catch (UnrecognizedPropertyException | JsonParseException e) {
            byteArrayInputStream = loadInputStream(bytes, byteArrayInputStream);
            data = (T) IOUtils.toString(byteArrayInputStream, StandardCharsets.UTF_8);
            logger.info((String) data);
        }
        return data;
    }

    private static <T> T handleResponse(Response response, Class<T> clazz) throws IOException {
        T data;
        byte[] bytes = IOUtils.toByteArray(response.body().byteStream());
        InputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        if (clazz.equals(String.class)) {
            return (T) IOUtils.toString(byteArrayInputStream, StandardCharsets.UTF_8);
        }
        try {
            data = jsonMapper.readValue(byteArrayInputStream, clazz);
        } catch (UnrecognizedPropertyException | JsonParseException e) {
            byteArrayInputStream = loadInputStream(bytes, byteArrayInputStream);
            data = (T) IOUtils.toString(byteArrayInputStream, StandardCharsets.UTF_8);
            logger.info((String) data);
        }
        return data;
    }

    public static InputStream loadInputStream(byte[] bytes, InputStream inputStream) throws IOException {
        if (inputStream.available() == 0) {
            return new ByteArrayInputStream(bytes);
        }
        return inputStream;
    }

    public static HttpStatus getHttpStatus(int statusCode) {
        HttpStatus status = HttpStatus.resolve(statusCode);
        if (status == null)
            return HttpStatus.INTERNAL_SERVER_ERROR;
        return status;
    }

}
