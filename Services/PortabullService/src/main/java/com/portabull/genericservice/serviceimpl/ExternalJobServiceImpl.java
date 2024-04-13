package com.portabull.genericservice.serviceimpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.dbutils.HibernateUtils;
import com.portabull.execption.BadRequestException;
import com.portabull.generic.models.TempJsonKeka;
import com.portabull.genericservice.jobs.DynamicClassLoader;
import com.portabull.genericservice.service.ExternalJobService;
import com.portabull.genericservice.service.JavaSourceFromString;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
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

    @Autowired
    HibernateUtils hibernateUtils;

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

    private static final String[] JARSFOLDERPATH = {"C:/Users/Administrator/.m2/repository/org/springframework/", "C:/Users/Administrator/.m2/repository/com/fasterxml/"
            };
    URL[] URLS;

    String CLASSPATH;

    @PostConstruct
    public void invokeJars() throws MalformedURLException {
        List<File> jarFiles = new ArrayList<>();
        for (int i = 0; i < JARSFOLDERPATH.length; i++) {
            jarFiles.addAll(getJarFiles(new File(JARSFOLDERPATH[i])));
        }
        // Construct the classpath including all JAR files
        StringBuilder classpath = new StringBuilder(System.getProperty("java.class.path"));
        List<String> jarFilePaths = new ArrayList<>();

        for (File jarFile : jarFiles) {
            jarFilePaths.add(jarFile.getAbsolutePath());
            classpath.append(File.pathSeparator).append(jarFile.getAbsolutePath());
        }
        ObjectMapper objectMapper = new ObjectMapper();
        URL[] urls = new URL[jarFilePaths.size()];
        for (int i = 0; i < jarFilePaths.size(); i++) {
            File jarFile = new File(jarFilePaths.get(i));
            urls[i] = jarFile.toURI().toURL();
        }
        URLS = urls;
        CLASSPATH = classpath.toString();
    }

    public static List<File> getJarFiles(File directory) {
        List<File> jarFiles = new ArrayList<>();
        if (directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    jarFiles.addAll(getJarFiles(file));
                } else if (file.isFile() && file.getName().endsWith(".jar")) {
                    jarFiles.add(file);
                }
            }
        }
        return jarFiles;
    }


    @Override
    public PortableResponse sendEmail(Map<String, Object> mailPayload) throws JsonProcessingException {
        EmailPayload emailPayload = new EmailPayload();

        if (mailPayload.get("to") instanceof List)
            emailPayload.setTo((List<String>) mailPayload.get("to"));
        else if (!mailPayload.get("to").toString().trim().startsWith("["))
            emailPayload.setTo(Arrays.asList(mailPayload.get("to").toString().trim()));
        else
            emailPayload.setTo(objectMapper.readValue(mailPayload.get("to").toString(), List.class));

        if (!StringUtils.isEmpty(mailPayload.get("cc"))) {
            if (mailPayload.get("cc") instanceof List)
                emailPayload.setCc((List<String>) mailPayload.get("cc"));
            else if (!mailPayload.get("cc").toString().trim().startsWith("["))
                emailPayload.setTo(Arrays.asList(mailPayload.get("cc").toString().trim()));
            else
                emailPayload.setTo(objectMapper.readValue(mailPayload.get("cc").toString(), List.class));
        }

        emailPayload.setBody(mailPayload.get("body") != null ? mailPayload.get("body").toString() : null);

        if (mailPayload.containsKey("isHTML"))
            emailPayload.setHtmlTemplete(String.valueOf(mailPayload.get("isHTML")).equalsIgnoreCase("true") ? true : false);

        emailPayload.setSubject(mailPayload.get("subject") != null ? getSubject(mailPayload.get("subject").toString()) : null);

        emailUtils.sendEmail(emailPayload);

        return new PortableResponse("", StatusCodes.C_200,
                PortableConstants.SUCCESS, "");
    }

    public String getSubject(String subject) {
        if (subject.contains("Date:{{@@}}")) {
            String date = "" + new Date();
            subject = subject.replace("Date:{{@@}}", date);
        }
        return subject;
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
    public PortableResponse executeCode(Map<String, String> codePayload) throws Exception {

        String code = codePayload.get("code");

        code = new String(Base64.getDecoder().decode(code.getBytes(StandardCharsets.UTF_8)));

        String dynamicClassName = codePayload.get("dynamicClassName");

        String result = executeDynamicCode(code, dynamicClassName);

        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, result);
    }

    @Override
    public Object tempEndpointKeka(String refreshToken, String flag) throws IOException {

        try {

            Map<String, Object> locationAddress = new HashMap<>();
            ResponseEntity<String> stringResponseEntity;
            locationAddress.put("longitude", 78.3722332);
            locationAddress.put("latitude", 17.540904);
            locationAddress.put("zip", "500090");
            locationAddress.put("countryCode", "IN");
            locationAddress.put("state", "Telangana");
            locationAddress.put("city", "Hyderabad");
            locationAddress.put("addressLine1", "Bachupally, Bachupalli Road, Bachupally, Hyderabad 500090, Telangana");
            locationAddress.put("addressLine2", "Hyderabad");

            Map<String, Object> locationAddressLogout = new HashMap<>();

            locationAddressLogout.put("longitude", 78.3856336);
            locationAddressLogout.put("latitude", 17.5504239);
            locationAddressLogout.put("zip", "500090");
            locationAddressLogout.put("countryCode", "IN");
            locationAddressLogout.put("state", "Telangana");
            locationAddressLogout.put("city", "Hyderabad");
            locationAddressLogout.put("addressLine1", "Bachupally, Lahari Green Park Road, Bachupally, Hyderabad 500090, Telangana");
            locationAddressLogout.put("addressLine2", "Hyderabad");


            String tempRefreshToken = "";

            List<TempJsonKeka> tempJsonKekas = hibernateUtils.loadFullData(TempJsonKeka.class);

            if (CollectionUtils.isEmpty(tempJsonKekas)) {
                TempJsonKeka tempJsonKeka = new TempJsonKeka();
                tempJsonKeka.setRefreshToken(refreshToken);
                hibernateUtils.saveOrUpdateEntity(tempJsonKeka);
                tempJsonKekas.add(tempJsonKeka);
            }

            tempRefreshToken = tempJsonKekas.get(0).getRefreshToken();

            if (StringUtils.isEmpty(tempRefreshToken)) {
                tempRefreshToken = refreshToken;
            }


            MultiValueMap<String, String> payload = new LinkedMultiValueMap<>();
            payload.put("grant_type", Arrays.asList("refresh_token"));
            payload.put("scope", Arrays.asList("offline_access kekahr.api hiro.api"));
            payload.put("refresh_token", Arrays.asList(tempRefreshToken));
            payload.put("client_id", Arrays.asList("987cc971-fc22-4454-99f9-16c078fa7ff6"));

            HttpHeaders he = new HttpHeaders();

            he.put("Content-Type", Arrays.asList("application/x-www-form-urlencoded"));

            he.put("Cookie", Arrays.asList(".AspNetCore.Identity.Application=CfDJ8FqpALfnMQpJuN2iskteUFw4iqrx2K2zHbcGQkh1atO5TZlg8gFyrHcXgi_J5ufqaCXzIvRQCLSS63lyfXfsZzYJSm7RjGXniGvIaru-6VSMagiWEePmL4dUMezxMEcxdQ5TwwhLLFgFcDwTpjXdMMJuiaiIC5hC5WXCzPTGtiQvSC2aF0ESlDAHFN0mBYKCNVteQKxCvvBVR-9dVOJRdqr5VLbi5qj09oSzVsQEbAmMl4Msd1J8IezujRMan2gYwJ_v0zhX1C7zuAUQM1wdAYlZP2S_-_1NGiodwx6iXuiopMzNyg5BmE5SFu6ojQd_ymQStvd9xl062WniJXMP8AsCz_zz8AlzMpfYsyHXZRnz9GoE5Lgx6gZv90VyJ6NJXxENxc8UWsfEgDoK34dA5JfMlExyEQPcBwRSWLN5UDI3QHowOl-k8RJVCrLvaGEbOenX6gTh26t7haPamHwIpm2ZWP4AuAXya8Xopj9ghx4EBr6Pb9z6iTGOyIS7-SVA7ldWdl8WfjfaWqYdkIwgom8GBsq0ObzzpIKPTYKyyy7Jj3Ua79vOWBV6O3mb8eqwePRZuInwBGcdTeZjVww5K0ehQPWGqKBaxhSS88gFHVs0xxzxkEXwz5EaHixWjMGu2r54OsgNDICnIhQhgrbDBJGe9b2TJk8e8k4k0_o6i5kQOO3l6a90_qikiAU7-kdFoW0UTLh6anm32BlNM8dN5nFhvifQyLddOD69A2R4dseWUE3LwSEV0606TVQp6wrHYqmHL9Hs-RYf-CS9a2novE95eNiiRk9isWpzI1ml_mRi7T3wyp2EyRevu2AkkJzU8B5ieFcZSz6eMTBy5Dz0gWwjLNA0RriJL0gOHTqNRKtVlzEK89jscpC9qtEx8KPqqFOjjGLstU8KLGlbAH3lzKDYLoVBlT_sYxDY8dB6pHIES-3u7bMPQlB9APq10rETZwBoCM4m3C_6bKlUmURy72BE4WLtFGiplx08Xzqe-p91rc4qRFLm1fHl6cj33epgYP4UG84HYiJbs5eR-E85WEoY7fAItFJ5UGdP6rUWOoUER6-_XczxhWeKhB8DuzdEsbZ7YlrXjNqkVVbyY42-QpgaSOvDTn6oD8o_X0IumLCtDSBHFQ2C8Umqo7xviSD5TJAb9xG4Qsq451K4qDlmcO3P1kumpbiE-1EHM0rmJg8M0e4GelXboSla7NsLVTAYTy-x76cQt74z5kxdUfb2lWt-KDtXXcbU7s--jrK2uiBb5QQRLMCFgXbjv49YzdaQJ2Fk-1sYSbdLaHirVM4_FqqpaEZiOo1zjAStufpj7Em3jcvLaNtzHnlPEOwraB8Dusik42W4grWpKbPbyKCpcrxE6LlDt6CbCEW8Kw1drqa-KLhLjScI2U6UY2KAdYlzidN3W97Ob1ZeUqYlslu2hjPcflvvepl6-c9Ho5FZVjHXpYwX_xPjBaNgVoIO3D6-pElGnyqsI8zleblGs5VRqd0YOQLow2vS2reePHizg77y2XHo5B_GvEwrEj-F_jIb3S9USmyBoJzGpTqnqbWeLTR6Yp2ZW0ob-OVikjRW_LDT; TiPMix=56.53782164744028; idsrv.session=EBF800D9CA154B3BC1B1190AED7A1F5F; x-ms-routing-name=self"));

            ResponseEntity<Map> response = template.postForEntity(
                    "https://app.keka.com/connect/token", new HttpEntity<>(payload, he), Map.class);

            if (response.getBody().containsKey("error")) {
                payload.put("refresh_token", Arrays.asList(refreshToken));
                response = template.postForEntity(
                        "https://app.keka.com/connect/token", new HttpEntity<>(payload, new HttpHeaders()), Map.class);
            }


            tempRefreshToken = response.getBody().get("refresh_token").toString();


            tempJsonKekas.get(0).setRefreshToken(tempRefreshToken);
            hibernateUtils.saveOrUpdateEntity(tempJsonKekas.get(0));

            String accessToken = response.getBody().get("access_token").toString();

            if (flag.equalsIgnoreCase("login")) {

                HttpHeaders loginheaders = new HttpHeaders();

                loginheaders.put("authorization", Arrays.asList("Bearer " + accessToken));

                loginheaders.put("cookie", Arrays.asList("Subdomain=datagaps.keka.com; ai_user=HL6sONPG4u1RAoH7RtXu64|2023-12-14T03:28:52.419Z; _clsk=13fkcaj%7C1706548729195%7C8%7C1%7Co.clarity.ms%2Fcollect; _clck=1t8ebxf%7C2%7Cfiu%7C0%7C1443; ai_session=6hB7sVeiiv58F/6jv+q9U+|1706587360936|1706587360936"));
                loginheaders.put("content-type", Arrays.asList("application/json; charset=UTF-8"));
                Map<String, Object> loginPayload = new HashMap<>();

                loginPayload.put("attendanceLogSource", 1);
                loginPayload.put("locationAddress", locationAddress);
                loginPayload.put("manualClockinType", 3);
                loginPayload.put("note", "");
                loginPayload.put("originalPunchStatus", 0);


                stringResponseEntity = template.postForEntity("https://datagaps.keka.com/k/attendance/api/mytime/attendance/remoteclockin"
                        , new HttpEntity<>(loginPayload, loginheaders), String.class);
            } else {

                HttpHeaders logoutheaders = new HttpHeaders();

                logoutheaders.put("authorization", Arrays.asList("Bearer " + accessToken));

                logoutheaders.put("cookie", Arrays.asList("Subdomain=datagaps.keka.com; ai_user=HL6sONPG4u1RAoH7RtXu64|2023-12-14T03:28:52.419Z; _clsk=13fkcaj%7C1706548729195%7C8%7C1%7Co.clarity.ms%2Fcollect; _clck=1t8ebxf%7C2%7Cfiu%7C0%7C1443; ai_session=6hB7sVeiiv58F/6jv+q9U+|1706587360936|1706587360936"));

                logoutheaders.put("content-type", Arrays.asList("application/json; charset=UTF-8"));

                Map<String, Object> loginPayload = new HashMap<>();

                loginPayload.put("attendanceLogSource", 1);
                loginPayload.put("locationAddress", locationAddressLogout);
                loginPayload.put("manualClockinType", 3);
                loginPayload.put("note", "");
                loginPayload.put("originalPunchStatus", 1);


                stringResponseEntity = template.postForEntity("https://datagaps.keka.com/k/attendance/api/mytime/attendance/remoteclockin"
                        , new HttpEntity<>(loginPayload, logoutheaders), String.class);
            }


            return stringResponseEntity.getBody();
        } catch (Exception e) {
            logger.error("", e);
        }

        return "failed";

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


    public String executeDynamicCode(String code, String dynamicClassName)
            throws Exception {

        String osTempLocation = prepareTempPath();

        if (!osTempLocation.endsWith(File.separator)) {
            osTempLocation = osTempLocation + File.separator;
        }

        String tempPath = osTempLocation + "java_temp_files" + File.separator;

        if (!new File(tempPath).exists()) {
            new File(tempPath).mkdirs();
        }

        String dynamicClassPath = tempPath + dynamicClassName;

        File javaFile = new File(dynamicClassPath + ".java");

        Files.write(javaFile.toPath(), code.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);


        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new RuntimeException("Java Compiler not found. Make sure you are using a JDK.");
        }

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null)) {
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(javaFile);
//            System.out.println(classpath.toString());
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, Arrays.asList("-cp", CLASSPATH), null, compilationUnits);
            if (!task.call()) {
                throw new RuntimeException("Compilation failed: " + diagnostics.getDiagnostics());
            }
        }

        String className = dynamicClassPath + ".class";
        Class<?> compiledClass = loadClass(className);

        // Instantiate the compiled class
        Object instance = compiledClass.getDeclaredConstructor().newInstance();

        // Call a method on the instantiated class
        Method method = compiledClass.getMethod("execute");

        Object invoke = method.invoke(instance);


        return invoke.toString();
    }

    private static void logCompilationFailure(String code, String className) {

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

        JavaFileObject compilationUnit = new JavaSourceFromString(className, code);

        // Perform the compilation
        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(compilationUnit);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);
        boolean success = task.call();

        if (!success) {
            logger.info("Compilation Failed Output :: ***********  ");
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                logger.info(diagnostic.getMessage(null));
            }
        }
    }

    public Class<?> loadClass(String classFilePath) throws Exception {
        ClassLoader classLoader = new DynamicClassLoader(URLS);
        Class<?> dynamicClass = classLoader.loadClass(classFilePath);
        return dynamicClass;
    }

}
