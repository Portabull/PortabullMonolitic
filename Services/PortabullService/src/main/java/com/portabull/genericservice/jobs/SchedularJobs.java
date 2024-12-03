package com.portabull.genericservice.jobs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portabull.dbutils.HibernateUtils;
import com.portabull.generic.models.SchedulerActions;
import com.portabull.generic.models.SchedulerTask;
import com.portabull.response.PortableResponse;
import com.portabull.utils.commonutils.CommonUtils;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@EnableScheduling
public class SchedularJobs {

    @Autowired
    private HibernateUtils hibernateUtils;

    static ObjectMapper objectMapper;

    static RestTemplate template;

    static String BASE_URL;

    @Value("${portabull.home.page.url}")
    public synchronized void setEmailFrom(String baseUrl) {
        SchedularJobs.BASE_URL = baseUrl;
    }

    static Logger logger = LoggerFactory.getLogger(SchedularJobs.class);

    static {
        try {

            objectMapper = new ObjectMapper();

            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(new TrustSelfSignedStrategy())
                    .build();

            // Create a HttpClient that ignores hostname verification
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLContext(sslContext)
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)  // Disable hostname verification
                    .build();

            // Set the HttpClient into RestTemplate
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

            List<SchedulerTask> dailyTriggers;
            List<SchedulerTask> timeToTimeTriggers;
            List<SchedulerTask> specificTimeTriggers;
            try (Session session = hibernateUtils.getSession()) {

                dailyTriggers = session.createQuery(" FROM SchedulerTask WHERE triggerType=:triggerType AND isActive=:isActive").
                        setParameter("triggerType", "D")
                        .setParameter("isActive", true).list();

                timeToTimeTriggers = session.createQuery(" FROM SchedulerTask WHERE triggerType=:triggerType AND isActive=:isActive").
                        setParameter("triggerType", "T")
                        .setParameter("isActive", true).list();


                specificTimeTriggers = session.createQuery(" FROM SchedulerTask WHERE triggerType=:triggerType AND isActive=:isActive AND lastTriggeredDate IS NULL").
                        setParameter("triggerType", "S")
                        .setParameter("isActive", true).list();
            }

            if (!CollectionUtils.isEmpty(dailyTriggers)) {
                checkDailyTasks(dailyTriggers, istZone);
            }

            dailyTriggers.clear();

            if (!CollectionUtils.isEmpty(timeToTimeTriggers)) {
                checkTimeToTimeTriggers(timeToTimeTriggers, istZone);
            }

            timeToTimeTriggers.clear();

            if (!CollectionUtils.isEmpty(specificTimeTriggers)) {
                checkSpecificTimeTriggers(specificTimeTriggers, istZone);
            }

        }

        threadCount.decrementAndGet();

        logger.info("SchedularJobs :: ended");
    }

    private void checkSpecificTimeTriggers(List<SchedulerTask> specificTimeTriggers, ZoneId istZone) {

        specificTimeTriggers.forEach(specificTimeTrigger -> {

            LocalDateTime now = LocalDateTime.now(istZone);

            Date istDate = Date.from(now.atZone(istZone).toInstant());

            if (!checkConfigAvailable(specificTimeTrigger, istDate))
                return;

            triggerTasks(specificTimeTrigger.getSchedulerActions(), specificTimeTrigger.getUserId());

            specificTimeTrigger.setLastTriggeredDate(istDate);

            hibernateUtils.saveOrUpdateEntity(specificTimeTrigger);

        });

    }

    private boolean checkConfigAvailable(SchedulerTask specificTimeTrigger, Date istDate) {

        if (specificTimeTrigger.getSpecificDate().getTime() >= istDate.getTime()) {
            return true;
        }

        return false;
    }

    private void checkTimeToTimeTriggers(List<SchedulerTask> timeToTimeTriggers, ZoneId istZone) {

        timeToTimeTriggers.forEach(timeToTimeTrigger -> {

            LocalDateTime now = LocalDateTime.now(istZone);

            Date istDate = Date.from(now.atZone(istZone).toInstant());

            if (!checkConfigAvailable(timeToTimeTrigger, now, istZone))
                return;

            triggerTasks(timeToTimeTrigger.getSchedulerActions(), timeToTimeTrigger.getUserId());

            timeToTimeTrigger.setLastTriggeredDate(istDate);

            hibernateUtils.saveOrUpdateEntity(timeToTimeTrigger);

        });

    }

    private boolean checkConfigAvailable(SchedulerTask task, LocalDateTime now, ZoneId istZone) {

        if (task.getLastTriggeredDate() == null) {
            return true;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(task.getLastTriggeredDate());
        calendar.add(Calendar.MINUTE, task.getTimeGap());

        if (calendar.getTime().getTime() >= Date.from(now.atZone(istZone).toInstant()).getTime()) {
            return true;
        }

        return false;
    }

    private void checkDailyTasks(List<SchedulerTask> dailyTriggerTime, ZoneId istZone) {

        dailyTriggerTime.forEach(dailyTrigger -> {

            LocalDateTime now = LocalDateTime.now(istZone);

            Date istDate = Date.from(now.atZone(istZone).toInstant());

            if (!checkConfigAvailable(dailyTrigger, now, istDate, istZone))
                return;

            triggerTasks(dailyTrigger.getSchedulerActions(), dailyTrigger.getUserId());

            dailyTrigger.setLastTriggeredDate(istDate);

            hibernateUtils.saveOrUpdateEntity(dailyTrigger);

        });
    }

    private void triggerTasks(Set<SchedulerActions> schedulerActions, Long userId) {

        schedulerActions.forEach(schedulerAction ->
                triggerTask(schedulerAction, userId)
        );

    }

    private void triggerTask(SchedulerActions schedulerAction, Long userId) {

        try {

            String actionType = schedulerAction.getAction_type();

            if ("M".equalsIgnoreCase(actionType)) {

                sendEmail(schedulerAction.getAction());

            } else if ("R".equalsIgnoreCase(actionType)) {

                executeRestAPI(objectMapper.readValue(schedulerAction.getAction(), new TypeReference<List<Map<String, Object>>>() {
                }));

            } else if ("C".equalsIgnoreCase(actionType)) {

                executeCode(schedulerAction.getAction(), userId);

            }

        } catch (Exception e) {
            logger.error("Exception :: ", e);
        }
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


    private void sendEmail(String mailPayload) {

        PortableResponse response = execute(BASE_URL + "gs/job/send-email", mailPayload);

        logger.info("sendEmail : {} ", response.getStatus());

    }

    private void executeRestAPI(List<Map<String, Object>> restPayloads) throws JsonProcessingException {

        PortableResponse response = execute(BASE_URL + "gs/job/execute-rest-api", restPayloads);

        logger.info("executeRestAPI : {}", response.getStatus());

    }

    public String executeCode(String action, Long userId) throws JsonProcessingException {

        String randomString = CommonUtils.getRandomString();

        String dynamicClassName = "DynamicClass" + randomString;

        String code = getCode(dynamicClassName, userId, action);

        Map<String, String> payload = new HashMap<>();

        payload.put("code", Base64.getEncoder().encodeToString(code.getBytes(StandardCharsets.UTF_8)));

        payload.put("dynamicClassName", dynamicClassName);

        PortableResponse response = execute(BASE_URL + "gs/job/execute-dynamic-code", payload);

        logger.info("executeCode : {}", response.getData().toString());

        return response.getData().toString();
    }

    private String getCode(String dynamicClassName, Long userId, String action) throws JsonProcessingException {

        StringBuilder codeBuilder = new StringBuilder();

        Map<String, String> map = objectMapper.readValue(action, Map.class);

        if (map.get("imports") != null) {
            codeBuilder.append(map.get("imports"));
        }

        codeBuilder.append(

                " public class " + dynamicClassName + " {" +
                        " Long userId = " + userId + "L;" +
                        "   public String execute() {" +
                        map.get("code") +
                        "   }" +


                        " public String saveCache(String key, String value) {" +

                        "Map<String, Object> payload = new HashMap<>();" +

                        "payload.put(\"key\", key);" +

                        "payload.put(\"value\", value);" +

                        "payload.put(\"userId\", userId);" +

                        "return executeCacheasfgawhgiasufuiashgfyuhfuyhwreyuhuyhfuhgfyuhgeyuryurgyugryufgwyuywfuguyfw(payload);" +

                        "}" +

                        "public String saveCache(String key, String value, boolean overwrite) {" +

                        " Map<String, Object> payload = new HashMap<>();" +

                        "payload.put(\"key\", key);" +

                        "payload.put(\"value\", value);" +

                        "payload.put(\"userId\", userId);" +

                        " payload.put(\"overwrite\", overwrite);" +

                        " return executeCacheasfgawhgiasufuiashgfyuhfuyhwreyuhuyhfuhgfyuhgeyuryurgyugryufgwyuywfuguyfw(payload);" +

                        "  }" +


                        "public String getCache(String key) {" +

                        " Map<String, Object> payload = new HashMap<>();" +

                        " payload.put(\"key\", key);" +

                        " payload.put(\"get\", true);" +

                        " payload.put(\"userId\", userId);" +

                        " return executeCacheasfgawhgiasufuiashgfyuhfuyhwreyuhuyhfuhgfyuhgeyuryurgyugryufgwyuywfuguyfw(payload);" +

                        "}" +


                        "public String executeCacheasfgawhgiasufuiashgfyuhfuyhwreyuhuyhfuhgfyuhgeyuryurgyugryufgwyuywfuguyfw(Map<String, Object> payload) {" +

                        "try {" +

                        "String url =" + "\"" + BASE_URL + "gs/save-cache" + "\";" +


                        "RestTemplate template = new RestTemplate();" +

                        " ResponseEntity<Map> response = template.postForEntity(url, new HttpEntity<>(payload,new HttpHeaders()), Map.class);" +

                        " if (!payload.containsKey(\"get\")) {" +
                        "return response.getBody().get(\"message\").toString();" +
                        "}" +

                        "return response.getBody().get(\"data\") != null ? response.getBody().get(\"data\").toString() : \"\";" +

                        "} catch (Exception e) {            return \"Connection issue please try again\";        }" +

                        " }" +

                        "}");


        return codeBuilder.toString();
    }

    private PortableResponse execute(String url, Object payload) {

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<PortableResponse> response = template.postForEntity(url, new HttpEntity<>(payload, headers), PortableResponse.class);

        return response.getBody();
    }


    public static void main1(String[] args) throws JsonProcessingException {

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


    //    public static void main(String[] args) {
//
//        String randomString = CommonUtils.getRandomString();
//
//
//        String dynamicClassName = "DynamicClass" + randomString;
//
//        String staticImports = "import java.util.*;import java.io.*;import java.math.*; import java.text.*; import java.util.concurrent.*;import javax.swing.*;import java.awt.*; import java.nio.*;   ";
//
//        staticImports = staticImports + "import org.springframework.http.*;import org.springframework.web.*;";
//
//        String code = staticImports + "public class " + dynamicClassName + " {" +
//                "   public String executeCode() {" +
//                "return \"hi\";" +
//                "   }" +
//                "}";
//
//        try {
//            String result = executeDynamicCode(code, dynamicClassName);
//            System.out.println("Result: " + result);
//        } catch (IOException | ClassNotFoundException | NoSuchMethodException |
//                 IllegalAccessException | InstantiationException | InvocationTargetException e) {
//            e.printStackTrace();
//        }
//    }
//
}



