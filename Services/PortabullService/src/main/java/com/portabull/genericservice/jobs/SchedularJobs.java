package com.portabull.genericservice.jobs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portabull.dbutils.HibernateUtils;
import com.portabull.execption.BadRequestException;
import com.portabull.generic.models.SchedulerActions;
import com.portabull.generic.models.SchedulerTask;
import com.portabull.payloads.EmailPayload;
import com.portabull.utils.emailutils.EmailUtils;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@EnableScheduling
public class SchedularJobs {

    @Autowired
    HibernateUtils hibernateUtils;

    @Autowired
    EmailUtils emailUtils;

    static ObjectMapper objectMapper;

    static RestTemplate template;
    static Logger logger = LoggerFactory.getLogger(SchedularJobs.class);

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

    private static final AtomicInteger threadCount = new AtomicInteger();

    @Scheduled(cron = "1 * * * * *")
    public void cronJobSch() {

        logger.info("SchedularJobs :: started");

        ZoneId istZone = ZoneId.of("Asia/Kolkata");

        threadCount.incrementAndGet();
        if (threadCount.get() > 5) {
            return;
        }

        synchronized (SchedularJobs.class) {
            List<SchedulerTask> dailyTriggers = null;
            try (Session session = hibernateUtils.getSession()) {

                dailyTriggers = session.createQuery(" FROM SchedulerTask WHERE triggerType=:triggerType AND isActive=:isActive").
                        setParameter("triggerType", "D")
                        .setParameter("isActive", true).list();

            }

            if (!CollectionUtils.isEmpty(dailyTriggers)) {
                checkDailyTasks(dailyTriggers, istZone);
            }
        }

        threadCount.decrementAndGet();

        logger.info("SchedularJobs :: ended");
    }

    private void checkDailyTasks(List<SchedulerTask> dailyTriggerTime, ZoneId istZone) {

        dailyTriggerTime.forEach(dailyTrigger -> {

            LocalDateTime now = LocalDateTime.now(istZone);

            Date istDate = Date.from(now.atZone(istZone).toInstant());

            if (!checkConfigAvailable(dailyTrigger, now, istDate, istZone))
                return;

            triggerTasks(dailyTrigger.getSchedulerActions());

            dailyTrigger.setLastTriggeredDate(istDate);

            hibernateUtils.saveOrUpdateEntity(dailyTrigger);

        });
    }

    private void triggerTasks(Set<SchedulerActions> schedulerActions) {

        schedulerActions.forEach(schedulerAction ->
                triggerTask(schedulerAction)
        );

    }

    private void triggerTask(SchedulerActions schedulerAction) {

        try {

            String actionType = schedulerAction.getAction_type();

            if ("M".equalsIgnoreCase(actionType)) {

                sendEmail(objectMapper.readValue(schedulerAction.getAction(), new TypeReference<Map<String, Object>>() {
                }));

            } else if ("R".equalsIgnoreCase(actionType)) {

                executeRestAPI(objectMapper.readValue(schedulerAction.getAction(), new TypeReference<List<Map<String, Object>>>() {
                }));

            }

        } catch (Exception e) {
            logger.error("Exception :: ", e);
        }
    }

    private void executeRestAPI(List<Map<String, Object>> restPayloads) throws JsonProcessingException {

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

    private void sendEmail(Map<String, Object> mailPayload) {

        EmailPayload emailPayload = new EmailPayload();

        emailPayload.setTo((List<String>) mailPayload.get("to"));

        emailPayload.setCc((List<String>) mailPayload.get("cc"));

        emailPayload.setBody(mailPayload.get("body") != null ? mailPayload.get("body").toString() : null);

        emailPayload.setSubject(mailPayload.get("subject") != null ? mailPayload.get("subject").toString() : null);

        emailUtils.sendEmail(emailPayload);

    }

    public static boolean checkConfigAvailable(SchedulerTask task, LocalDateTime now, Date istDate, ZoneId istZone) {

        String[] daysList = task.getDays().split(",");

        for (int i = 0; i < daysList.length; i++) {
            String day = daysList[i];

            if (now.getDayOfWeek().name().equalsIgnoreCase(getDay(day.trim()))) {

                if (task.getLastTriggeredDate() == null) {

                    String[] time = task.getSpecificDailyTime().split(":");

                    if (LocalTime.now(istZone).compareTo(LocalTime.of(Integer.valueOf(time[0]), Integer.valueOf(time[1]), Integer.valueOf(time[2]))) >= 0)
                        return true;

                } else if (!task.getLastTriggeredDate().toInstant().atZone(istZone).toLocalDate()
                        .isEqual(istDate.toInstant().atZone(istZone).toLocalDate())) {
                    //checking if current date is already executed or not

                    String[] time = task.getSpecificDailyTime().split(":");

                    if (LocalTime.now(istZone).compareTo(LocalTime.of(Integer.valueOf(time[0]), Integer.valueOf(time[1]), Integer.valueOf(time[2]))) >= 0)
                        return true;
                }
            }
        }

        return false;
    }

    public static String getDay(String day) {

        switch (day) {
            case "mo":
                return "MONDAY";
            case "tu":
                return "TUESDAY";
            case "we":
                return "WEDNESDAY";
            case "th":
                return "THURSDAY";
            case "fr":
                return "FRIDAY";
            case "sa":
                return "SATURDAY";
            case "su":
                return "SUNDAY";
            default:
                return "";
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

    private Number getFormattedValue(Object v) {

        if (v == null)
            return null;


        if (v instanceof Integer) {
            return Integer.valueOf(v.toString());
        } else if (v instanceof Long) {
            return Long.valueOf(v.toString());
        } else if (v instanceof Float) {
            return Float.valueOf(v.toString());
        } else if (v instanceof Double) {
            return Double.valueOf(v.toString());
        } else if (v instanceof Byte) {
            return Byte.valueOf(v.toString());
        } else if (v instanceof BigDecimal) {
            return new BigDecimal(v.toString());
        } else if (v instanceof BigInteger) {
            return new BigInteger(v.toString());
        }


        return 0;
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

    public static void main(String[] args) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        List<String> matchers = objectMapper.readValue("[    \"response->mapper->val->st,1\",    \"response->tr->sd,2\"]", List.class);

        List<Object> responses = objectMapper.readValue("[    {\"mapper\": {  \"val\": { \"st\": 25            }        }    },    {        \"tr\": { \"sd\": \"hi\"        }    }]", List.class);

        Map<String, Object> result = new HashMap<>();

        for (int i = 0; i < matchers.size(); i++) {

            String match = matchers.get(i);

            String[] responseWithDependency = match.split(",");

            String code = responseWithDependency[0];

            Object o = responses.get(Integer.valueOf(responseWithDependency[1]) - 1);

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

            result.put(match, finalDependency);

        }

        objectMapper.writeValueAsString(result);


//        try {
//            // Define the Java code as a string with ObjectMapper usage
//            String javaCode = "import java.util.*;import com.fasterxml.jackson.databind.ObjectMapper;\n" +
//                    "public class DynamicCodeWithResult {\n" +
//                    "    public String execute() {\n" +
//                    "        ObjectMapper objectMapper = new ObjectMapper();\n" +
//                    "        try {\n" +
//                    "            String jsonString = \"{\\\"messag4e\\\": \\\"Hello from dynamically executed code!\\\"}\";\n" +
//                    "            Message message = objectMapper.readValue(jsonString, Message.class);\n" +
//                    "            return message.getMessage();\n" +
//                    "        } catch (Exception e) {\n" +
//                    "            e.printStackTrace();\n" +
//                    "            return \"Error in execution\";\n" +
//                    "        }\n" +
//                    "    }";
//
//            // Save the code to a temporary file with a .java extension
//            String fileName = "DynamicCodeWithResult.java";
//            Path filePath = Paths.get(fileName);
//            try (FileWriter writer = new FileWriter(fileName)) {
//                writer.write(javaCode);
//            }
//
//            // Capture the output using a ByteArrayOutputStream
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//
//            // Compile the code using JavaCompiler
//            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//            compiler.run(null, null, null, filePath.toString());
//
//            // Load and execute the compiled class using URLClassLoader
//            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{new File("").toURI().toURL()});
//            Class<?> dynamicClass = Class.forName("DynamicCodeWithResult", true, classLoader);
//
//            // Create an instance of the dynamically loaded class
//            Object instance = dynamicClass.getDeclaredConstructor().newInstance();
//
//            // Redirect System.out to the captured output stream
//            System.setOut(new java.io.PrintStream(outputStream));
//
//            // Invoke the execute method of the dynamically loaded class
//            String result = (String) dynamicClass.getMethod("execute").invoke(instance);
//
//            // Reset System.out to its original value
//            System.setOut(new java.io.PrintStream(new java.io.FileOutputStream(java.io.FileDescriptor.out)));
//
//            // Display the captured result
//            System.out.println("Result: " + result);
//
//            // Clean up: Delete the temporary .java and .class files
//            Files.deleteIfExists(filePath);
//            Files.deleteIfExists(Paths.get("DynamicCodeWithResult.class"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }


//    public static void main(String[] args) {
//
//        String className = "DynamicCodeWithResult";
//
//        String fileName = className + ".java";
//
//        String[] executeArgs = {
//                "[    \"response->mapper->val->st,1\",    \"response->tr->sd,2\"]",
//                "[    {\"mapper\": {  \"val\": { \"st\": 25            }        }    },    {        \"tr\": { \"sd\": \"hi\"        }    }]"
//        };
//
//
//        try {
//            // Define the Java code as a string with ObjectMapper usage
//            String javaCode = "import java.util.*;import com.fasterxml.jackson.databind.ObjectMapper;\n" +
//                    "public class "+  className + " {\n" +
//                    "    public String execute(String[] args) {\n" +
//
//                    "        try {\n" +
//                    " ObjectMapper objectMapper = new ObjectMapper();\n" +
//                    "\n" +
//                    "        List<String> matchers = objectMapper.readValue(args[0], List.class);\n" +
//                    "\n" +
//                    "        List<Object> responses = objectMapper.readValue(args[1], List.class);\n" +
//                    "\n" +
//                    "        Map<String, Object> result = new HashMap<>();\n" +
//                    "        \n" +
//                    "        for (int i = 0; i < matchers.size(); i++) {\n" +
//                    "\n" +
//                    "            String match = matchers.get(i);\n" +
//                    "\n" +
//                    "            String[] responseWithDependency = match.split(\",\");\n" +
//                    "\n" +
//                    "            String code = responseWithDependency[0];\n" +
//                    "\n" +
//                    "            Object o = responses.get(Integer.valueOf(responseWithDependency[1]) - 1);\n" +
//                    "\n" +
//                    "            Map<String, Object> dependencyResponse;\n" +
//                    "            if (o instanceof Map) {\n" +
//                    "                dependencyResponse = (Map<String, Object>) o;\n" +
//                    "            } else {\n" +
//                    "                dependencyResponse = objectMapper.readValue(o.toString(), Map.class);\n" +
//                    "            }\n" +
//                    "            \n" +
//                    "            String[] tags = code.split(\"->\");\n" +
//                    "\n" +
//                    "            Object finalDependency = null;\n" +
//                    "\n" +
//                    "            for (int j = 1; j < tags.length; j++) {\n" +
//                    "\n" +
//                    "                String tag = tags[j];\n" +
//                    "\n" +
//                    "                if (dependencyResponse != null && j == 1)\n" +
//                    "                    finalDependency = dependencyResponse.get(tag);\n" +
//                    "                else if (finalDependency != null && finalDependency instanceof Map)\n" +
//                    "                    finalDependency = ((Map<String, Object>) finalDependency).get(tag);\n" +
//                    "\n" +
//                    "            }\n" +
//                    "\n" +
//                    "            result.put(match, finalDependency);\n" +
//                    "            \n" +
//                    "        }\n" +
//                    "\n" +
//                    "       return objectMapper.writeValueAsString(result);" +
//                    "        } catch (Exception e) {\n" +
//                    "            e.printStackTrace();\n" +
//                    "            return \"Error in execution\";\n" +
//                    "        }\n" +
//                    "    }}";
//
//
//            Path filePath = Paths.get(fileName);
//            try (FileWriter writer = new FileWriter(fileName)) {
//                writer.write(javaCode);
//            }
//
//            // Capture the output using a ByteArrayOutputStream
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//
//            // Compile the code using JavaCompiler
//            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//            compiler.run(null, null, null, filePath.toString());
//
//            // Load and execute the compiled class using URLClassLoader
//            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{new File("").toURI().toURL()});
//            Class<?> dynamicClass = Class.forName(className, true, classLoader);
//
//            // Create an instance of the dynamically loaded class
//            Object instance = dynamicClass.getDeclaredConstructor().newInstance();
//
//            // Redirect System.out to the captured output stream
//            System.setOut(new java.io.PrintStream(outputStream));
//
//            // Invoke the execute method of the dynamically loaded class
//            String result = (String) dynamicClass.getMethod("execute").invoke(instance, (Object) executeArgs);
//
//            // Reset System.out to its original value
//            System.setOut(new java.io.PrintStream(new java.io.FileOutputStream(java.io.FileDescriptor.out)));
//
//            // Display the captured result
//            System.out.println("Result: " + result);
//
//            // Clean up: Delete the temporary .java and .class files
//            Files.deleteIfExists(filePath);
//            Files.deleteIfExists(Paths.get(className + ".class"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
}


//    public static void main(String[] args) {
//        try {
//            // Define the Java code as a string
//            String javaCode = "public class HelloWorld { public static void main(String[] args) { System.out.println(\"Hello,hdysyrsesryesryyyyyyyyyyyyyyyyyyyyyyyyyy World!\"); } }";
//
//            // Save the code to a temporary file with a .java extension
//            String fileName = "HelloWorld.java";
//            Path filePath = Paths.get(fileName);
//            try (FileWriter writer = new FileWriter(fileName)) {
//                writer.write(javaCode);
//            }
//
//            // Redirect System.out to capture the output
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            PrintStream printStream = new PrintStream(outputStream);
//            PrintStream originalOut = System.out;
//            System.setOut(printStream);
//
//            // Compile the code using JavaCompiler
//            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//            compiler.run(null, null, null, filePath.toString());
//
//            // Load and execute the compiled class using URLClassLoader
//            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{new File("").toURI().toURL()});
//            Class<?> dynamicClass = Class.forName("HelloWorld", true, classLoader);
//
//            // Invoke the main method of the dynamically loaded class
//            dynamicClass.getMethod("main", String[].class).invoke(null, (Object) null);
//
//            // Reset System.out to its original value
//            System.setOut(originalOut);
//
//            // Get the captured output as a string
//            String output = outputStream.toString();
//            System.out.println("Captured Output: " + output);
//
//            // Clean up: Delete the temporary .java and .class files
//            Files.deleteIfExists(filePath);
//            Files.deleteIfExists(Paths.get("HelloWorld.class"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


//    public static void main(String[] args) {
//        String input = "This is a {{somestring}} and another {{example}}.";
//
//        // Define the regular expression pattern
//        String pattern = "\\{\\{([^}]+)}}";
//
//        // Compile the pattern
//        Pattern curlyBracePattern = Pattern.compile(pattern);
//
//        // Match the pattern against the input string
//        Matcher matcher = curlyBracePattern.matcher(input);
//
//        // Find all matches
//        while (matcher.find()) {
//            // Extract the matched content within double curly braces
//            String match = matcher.group(1);
//            System.out.println("Match found: " + match);
//        }
//    }