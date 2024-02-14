package com.portabull.genericservice.serviceimpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.execption.BadRequestException;
import com.portabull.genericservice.jobs.DynamicClassLoader;
import com.portabull.genericservice.service.ExternalJobService;
import com.portabull.payloads.EmailPayload;
import com.portabull.response.PortableResponse;
import com.portabull.utils.emailutils.EmailUtils;
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
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ExternalJobServiceImpl implements ExternalJobService {

    @Autowired
    EmailUtils emailUtils;

    static ObjectMapper objectMapper;

    static Logger logger = LoggerFactory.getLogger(ExternalJobServiceImpl.class);

    static RestTemplate template;

    static {
        try {

            objectMapper = new ObjectMapper();

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


    @Override
    public PortableResponse sendEmail(Map<String, Object> mailPayload) {
        EmailPayload emailPayload = new EmailPayload();

        emailPayload.setTo((List<String>) mailPayload.get("to"));

        emailPayload.setCc((List<String>) mailPayload.get("cc"));

        emailPayload.setBody(mailPayload.get("body") != null ? mailPayload.get("body").toString() : null);

        emailPayload.setSubject(mailPayload.get("subject") != null ? mailPayload.get("subject").toString() : null);

        emailUtils.sendEmail(emailPayload);

        return new PortableResponse("", StatusCodes.C_200,
                PortableConstants.SUCCESS, "");
    }

    @Override
    public PortableResponse executeRestAPI(List<Map<String, Object>> restPayloads) throws JsonProcessingException {

        List<String> restResponses = new ArrayList<>();

        for (Map<String, Object> restPayload : restPayloads) {

            HttpEntity entity;

            String url = getStringDependencyResponse(restPayload.get("url").toString(), restResponses);

            HttpHeaders httpHeaders = getHeaders((Map<String, String>) restPayload.get("headers"), restResponses);

            Object body = getBody(restPayload.get("body"), restResponses);

            String method = restPayload.get("method").toString();

            if (body != null && !StringUtils.isEmpty(body)) {
                entity = new HttpEntity<>(body, httpHeaders);
            } else {
                entity = new HttpEntity(httpHeaders);
            }

            ResponseEntity<String> exchange = template.exchange(url, getHttpMethod(method), entity, String.class);

            restResponses.add(exchange.getBody());

        }

        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, "");
    }

    @Override
    public PortableResponse executeCode(Map<String, String> codePayload) throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {

        String code = codePayload.get("code");

        String dynamicClassName = codePayload.get("dynamicClassName");

        String result = executeDynamicCode(code, dynamicClassName);

        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, result);
    }


    private String getStringDependencyResponse(String data, List<String> restResponses) throws JsonProcessingException {

        if (!CollectionUtils.isEmpty(restResponses))
            return data;

        List<String> matchers = getMatchers(data);

        if (!CollectionUtils.isEmpty(matchers))
            return data;

        StringBuilder builder = new StringBuilder(data);

        Map<String, Object> depResp = prepareDependencyResponse(matchers, restResponses);

        depResp.forEach((k, v) -> {

            String tempUrl = builder.toString();

            tempUrl.replaceAll(k, v != null ? v.toString() : "");

            builder.delete(0, builder.length());

            builder.append(tempUrl);

        });

        return builder.toString();

    }

    private Map<String, Object> prepareDependencyResponse(List<String> matchers, List<String> restResponses) throws JsonProcessingException {

        Map<String, Object> result = new HashMap<>();

        for (int i = 0; i < matchers.size(); i++) {

            String match = matchers.get(i);

            String[] responseWithDependency = match.split(",");

            String code = responseWithDependency[0];

            Object o = restResponses.get(Integer.valueOf(responseWithDependency[1]) - 1);

            Map<String, Object> dependencyResponse;
            if (o instanceof Map) {
                dependencyResponse = (Map<String, Object>) o;
            } else {
                dependencyResponse = objectMapper.readValue(o.toString(), Map.class);
            }

            String[] tags = code.split("->");

            Object finalDependency = null;

            for (int j = 1; j < tags.length; j++) {

                String tag = tags[j];

                if (dependencyResponse != null && j == 1)
                    finalDependency = dependencyResponse.get(tag);
                else if (finalDependency != null && finalDependency instanceof Map)
                    finalDependency = ((Map<String, Object>) finalDependency).get(tag);

            }

            result.put("{{" + match + "}}", finalDependency);

        }

        return result;
    }

    public List<String> getMatchers(String data) {

        List<String> matchers = new ArrayList<>();

        Matcher matcher = Pattern.compile("\\{\\{([^}]+)}}").matcher(data);

        while (matcher.find()) {
            matchers.add(matcher.group(1));
        }

        return matchers;
    }


    private HttpHeaders getHeaders(Map<String, String> headers, List<String> restResponses) throws JsonProcessingException {
        HttpHeaders httpHeaders = new HttpHeaders();

        if (CollectionUtils.isEmpty(headers) || CollectionUtils.isEmpty(restResponses))
            return httpHeaders;

        List<String> matchers = new ArrayList<>();
        for (String value : headers.values()) {
            if (value != null) {
                value = value.trim();
                if (value.startsWith("{{") && value.endsWith("}}")) {
                    List<String> matchers1 = getMatchers(value);
                    matchers.addAll(matchers1);
                }
            }
        }

        Map<String, Object> data = prepareDependencyResponse(matchers, restResponses);

        headers.forEach((hk, hv) -> {
            if (data.containsKey(hv)) {
                httpHeaders.put(hk, Arrays.asList(data.get(hv).toString()));
            } else {
                httpHeaders.put(hk, Arrays.asList(hv));
            }
        });

        return httpHeaders;
    }

    private Object getBody(Object body, List<String> restResponses) throws JsonProcessingException {

        if (body == null) {
            return body;
        }

        String stringBody;
        if (body instanceof List) {
            stringBody = getStringDependencyResponse(objectMapper.writeValueAsString(body), restResponses);
            return objectMapper.readValue(stringBody, List.class);
        } else if (body instanceof Map) {
            stringBody = getStringDependencyResponse(objectMapper.writeValueAsString(body), restResponses);
            return objectMapper.readValue(stringBody, Map.class);
        } else {
            stringBody = getStringDependencyResponse(body.toString(), restResponses);
        }

        return stringBody;
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

    public static String prepareTempPath() {
        String tmpDir = System.getProperty("java.io.tmpdir");
        if (!new File(tmpDir).exists()) {
            new File(tmpDir).mkdirs();
        }
        return tmpDir;
    }


    public static String executeDynamicCode(String code, String dynamicClassName)
            throws IOException, ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InstantiationException, InvocationTargetException {

        String tempPath = prepareTempPath() + File.separator + "java_temp_files" + File.separator;

        if (!new File(tempPath).exists()) {
            new File(tempPath).mkdirs();
        }

        String dynamicClassPath = tempPath + dynamicClassName;

        File javaFile = new File(dynamicClassPath + ".java");

        Files.write(javaFile.toPath(), code.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        PrintStream originalOut = System.out;
        System.setOut(printStream);

        try {
            logger.info("********************************************************");
            logger.info(code);
            logger.info("********************************************************");
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            int compilationResult = compiler.run(null, null, null, javaFile.getAbsolutePath());

            if (compilationResult == 0) {
                ClassLoader classLoader = new DynamicClassLoader();
                Class<?> dynamicClass = classLoader.loadClass(dynamicClassPath + ".class");

                Object instance = dynamicClass.getDeclaredConstructor().newInstance();
                Method getMessageMethod = dynamicClass.getMethod("executeCode");
                Object result = getMessageMethod.invoke(instance);

                return result.toString();

            } else {
                System.err.println("Compilation failed");
                return "Compilation failed";
            }
        } finally {
            // Restore the original standard output
//            javaFile.delete();
//            new File(dynamicClassPath + ".class").delete();
            System.setOut(originalOut);
            printStream.close();
        }
    }
}
