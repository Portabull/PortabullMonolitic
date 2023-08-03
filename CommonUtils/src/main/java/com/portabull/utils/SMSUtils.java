package com.portabull.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SMSUtils {

    public static String sendSms(String apiKey, String message, String sender, String numbers) {
        try {
            // Construct data


            ResponseEntity<String> data = new RestTemplate().postForEntity("https://api.textlocal.in/send/?" + message + numbers + sender + apiKey, new HttpEntity<Object>(new HttpHeaders()), String.class);
            return data.getBody();
            // Send data
//            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.textlocal.in/send/?").openConnection();
//            String data = message + numbers  + sender + apiKey;
//            conn.setDoOutput(true);
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
//            conn.getOutputStream().write(data.getBytes("UTF-8"));
//            final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            final StringBuffer stringBuffer = new StringBuffer();
//            String line;
//            while ((line = rd.readLine()) != null) {
//                stringBuffer.append(line);
//            }
//            rd.close();
//
//            return stringBuffer.toString();
        } catch (Exception e) {
            System.out.println("Error SMS " + e);
            return "Error " + e;
        }
//        return null;
    }

    public static void main(String args[]) throws IOException {
//        String myApiKey = "NzcyZDdkMDEwZjkwOWE2ZTUwYzhjNDgxMWNkYmZjMjg=";
//
//
//        String message = "message=" + "I am a template!";
//        String sender = "&sender=" + "message";
////        String sender = "";
//        String numbers = "&numbers=" + "+918500569237";
//
//
//        String apiKey = "&apikey=" + myApiKey;
//        System.out.println(SMSUtils.sendSms(apiKey, message, sender, numbers));
//
//        System.out.println(SMSUtils.getTemplates());

        sendSMS();
    }

    private static void sendSMS() throws IOException {
//
//        OkHttpClient client = new OkHttpClient();
//
//        Request request = new Request.Builder()
//                .url("https://twilio-sms.p.rapidapi.com/2010-04-01/Accounts/%7BAccountSid%7D/Messages.json?to=8500569237&body=yjfufjhgjh&from=8500569235")
//                .get()
//                .addHeader("x-rapidapi-key", "7987a5fc8bmshcea5687a0a54ea1p137700jsna256334692eb")
//                .addHeader("x-rapidapi-host", "twilio-sms.p.rapidapi.com")
//                .build();


        HttpHeaders headers = new HttpHeaders();
        headers.set("x-rapidapi-key", "7987a5fc8bmshcea5687a0a54ea1p137700jsna256334692eb");

        headers.set("x-rapidapi-host", "twilio-sms.p.rapidapi.com");

        ResponseEntity responseEntity = new RestTemplate().postForEntity("https://twilio-sms.p.rapidapi.com/2010-04-01/Accounts/%7BAccountSid%7D/Messages.json?to=8500569237&body=yjfufjhgjh&from=8500569235",
                new HttpEntity<Object>(headers),String.class);


        responseEntity.getBody();
//        Response response = client.newCall(request).execute();
    }

    public static String getTemplates() {
        try {
            // Construct data
            String apiKey = "apikey=" + "NzcyZDdkMDEwZjkwOWE2ZTUwYzhjNDgxMWNkYmZjMjg=";

            // Send data
            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.textlocal.in/get_templates/?").openConnection();
            String data = apiKey;
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
            conn.getOutputStream().write(data.getBytes("UTF-8"));
            final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                stringBuffer.append(line);
            }
            rd.close();

            return stringBuffer.toString();
        } catch (Exception e) {
            System.out.println("Error SMS " + e);
            return "Error " + e;
        }
    }


}
