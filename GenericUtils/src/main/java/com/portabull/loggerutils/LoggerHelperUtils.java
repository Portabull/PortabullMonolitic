package com.portabull.loggerutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class LoggerHelperUtils {

    static String loggingFilePath;

    @Value("${logging.file.path:null}")
    public void setLoggingFilePath(String loggingFile) {
        loggingFilePath = loggingFile;
    }

    public static String getLoggingFilePath() {
        return loggingFilePath;
    }

    public static void saveToFile(String s, Throwable throwable) {
        Map<String, Object> exception = new LinkedHashMap<>();
        exception.put("exceptionMessage", s);
        exception.put("stackTrace", throwable.getMessage());
        try {
            String contextPath;
            HttpServletRequest currentRequest = LoggerHelperUtils.getCurrentRequest();
            if (currentRequest != null) {
                contextPath = currentRequest.getContextPath() + currentRequest.getServletPath();
            } else {
                contextPath = "contextPathNull";
            }
            exception.put("contextPath", contextPath);
            String json = new ObjectMapper().writeValueAsString(exception);


            if (LoggerHelperUtils.getLoggingFilePath() != null) {
                File te = new File(LoggerHelperUtils.getLoggingFilePath() + File.separator + contextPath + File.separator);
                if (!te.exists()) {
                    te.mkdirs();
                }

                String filePath = LoggerHelperUtils.getLoggingFilePath() + contextPath + File.separator + "dfg.txt";
                File file = new File(filePath);
                if (file.exists()) {
                    Path path = Paths.get(filePath);
                    LoggerHelperUtils.appendToFile(path, json + System.lineSeparator());
                } else {
                    LoggerHelperUtils.writeFile(file, json);
                }


            }
        } catch (Exception e) {

        }

    }


    public static void writeFile(File file, String text) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        bw.write(text);
        bw.close();
    }

    public static HttpServletRequest getCurrentRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        return null;
    }

    public static void appendToFile(Path path, String content)
            throws IOException {

        Files.write(path, content.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);

    }

}
