package com.portabull.genericservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portabull.genericservice.service.ExternalJobService;
import com.portabull.response.PortableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("gs/job")
public class ExternalJobController {

    @Autowired
    ExternalJobService jobService;

    @PostMapping("send-email")
    public ResponseEntity<PortableResponse> sendEmail(@RequestBody Map<String, Object> mailPayload) throws JsonProcessingException {

        return new ResponseEntity<>(jobService.sendEmail(mailPayload), HttpStatus.OK);

    }

    @PostMapping("execute-rest-api")
    public ResponseEntity<PortableResponse> executeRestAPI(@RequestBody List<Map<String, Object>> restPayloads) throws JsonProcessingException {

        return new ResponseEntity<>(jobService.executeRestAPI(restPayloads), HttpStatus.OK);

    }

    @PostMapping("execute-dynamic-code")
    public ResponseEntity<PortableResponse> executeCode(@RequestBody Map<String, String> codePayload) throws Exception {

        return new ResponseEntity<>(jobService.executeCode(codePayload), HttpStatus.OK);

    }

    @PostMapping("temp-endpoint-keka")
    public ResponseEntity<Object> tempEndpointKeka(@RequestParam String refreshToken, @RequestParam String flag) throws IOException {

        return new ResponseEntity<>(jobService.tempEndpointKeka(refreshToken, flag), HttpStatus.OK);

    }

    @GetMapping(value = "groww/market/updates", produces = "text/html")
    public String tempEndpointKeka(@RequestParam(required = false) String sent) throws IOException {
        return execute(sent);
    }

    public String execute(String sent) {
        String htmlHeder = "<!DOCTYPE html>\r\n" + " <html>\r\n" + " <link href=\"https://fonts.googleapis.com/css? family=Open+Sans:300, 400, 700\" rel=\"stylesheet\" type=\"text/css\"/>\r\n" + "<style>\r\n" + "@charset \"UTf-8\";\r\n" + "@import url(https://fonts.googleapis.com/css?font-family=\"Open+Sans:300, 400, 700\");\r\n" + "body{\r\n" + "  font-family: \"Open Sans\", sans-serif;\r\n" + "  font-weight: 300;\r\n" + "  line-height: 1.42em;\r\n" + "  color: #A7A1AE;\r\n" + "  background: #1F2739;\r\n" + "}\r\n" + "h1{\r\n" + "  font-size: 3em;\r\n" + "  font-weight: 300;\r\n" + "  text-align: center;\r\n" + "  display: block;\r\n" + "  line-height: 1em;\r\n" + "  padding-bottom: 2em;\r\n" + "  color: #FB667A;\r\n" + "}\r\n" + "h2 a{\r\n" + "  font-weight: 700;\r\n" + "  text-transform: uppercase;\r\n" + "  color: #FB667A;\r\n" + "  text-decoration: none;\r\n" + "\r\n" + "}\r\n" + ".blue{color: #185875;}\r\n" + ".yellow{color: #FfF842;}\r\n" + "\r\n" + ".container th h1{\r\n" + "  font-weight: bold;\r\n" + "  font-size: 1em;\r\n" + "  text-align: left;\r\n" + "  color: #185875;\r\n" + "\r\n" + "}\r\n" + ".container td{\r\n" + "  font-weight: normal;\r\n" + "  font-size: 1em;\r\n" + "  -webkit-box-shadow:0 2px 2px-2px #0E1119;\r\n" + "  -moz-box-shadow:0 2px 2px -2px #0E1119;\r\n" + "  box-shadow: 0 2px 2px -2px #0E1119;\r\n" + "\r\n" + "}\r\n" + ".container{\r\n" + "  text-align: left;\r\n" + "  overflow: hidden;\r\n" + "  width: 80%;\r\n" + "  margin: 0 auto;\r\n" + "  display: table;\r\n" + "  padding: 0 0 8em 0;\r\n" + "\r\n" + "}\r\n" + ".container td, .container th{\r\n" + "  padding-bottom: 2%;\r\n" + "  padding-top: 2%;\r\n" + "  padding-left: 2%;\r\n" + "\r\n" + "}\r\n" + "/*Background-color of the odd rows */\r\n" + ".container tr:nth-child(odd){\r\n" + "  background-color: #323C50;\r\n" + "\r\n" + "}\r\n" + "/*Background-color of the even rows*/\r\n" + ".container tr:nth-child(even){\r\n" + "  background-color: #2C3446;\r\n" + "}\r\n" + ".container th{\r\n" + "  background-color: #1F2739;\r\n" + "\r\n" + "}\r\n" + ".container td:first-child{color: #FB667A;}\r\n" + "\r\n" + ".container tr:hover{\r\n" + "  background-color: #464A52;\r\n" + "  -webkit-box-shadow:0 6px 6px -6px #0E1119;\r\n" + "  -moz-box-shadow:0 6px 6px -6px #0E1119;\r\n" + "  box-shadow: 0 6px 6px -6px #0E1119;\r\n" + "}\r\n" + "\r\n" + ".container td:hover{\r\n" + "  background-color: #FFF842;\r\n" + "  color: #403E10;\r\n" + "  font-weight: bold;\r\n" + "  box-shadow: #7F7C21 -1px  1px, #7F7C21 -2px 2px, #7F7C21 -3px 3px, #7F7C21 -4px 4px, #7F7C21 -5px 5px, #7F7C21 -6px 6px;\r\n" + "  transform: translate3d(6px, -6px, 0);\r\n" + "  transition-delay: 0s;\r\n" + "  transition-duration: 0.4s;\r\n" + "  transition-property: all;\r\n" + "  transition-timing-function: line;\r\n" + "}\r\n" + "@media(max-width: 800px){\r\n" + "  .container td:nth-child(4),\r\n" + "  .container th:nth-child(4){display: none;}\r\n" + "\r\n" + "  }\r\n" + "}\r\n" + "</style>\r\n" + " <h1><span class=\"yellow\">Stock Market Updates</pan></h1><br>       <a href=\"https://portabull.in/APIGateway/gs/job/groww/market/updates\"  target=\"_blank\">Market Latest Updates</a>      \r\n" + "   <table class=\"container\">\r\n" + "    <thead>\r\n" + "      <tr>\r\n" + "        <th><h1>Company Name</h1></th>\r\n" + "        <th><h1>Percentage</h1></th>\r\n" + "           <th><h1>Current Price</h1></th>\r\n" + "      </tr>\r\n" + "    </thead>  <tbody>";
        String htmlFooter = " </tbody>\r\n" + "   </table>\r\n" + "    </tbody>\r\n" + "    </html>";
        RestTemplate template = new RestTemplate();
        Map<String, Object> payload = new HashMap<>();
        Map<String, Object> listFilters = new HashMap<>();
        listFilters.put("INDUSTRY", Arrays.asList());
        listFilters.put("INDEX", Arrays.asList());
        payload.put("listFilters", listFilters);
        Map<String, Object> objFilters = new HashMap<>();
        payload.put("objFilters", objFilters);
        Map<String, Object> CLOSE_PRICE = new HashMap<>();
        Map<String, Object> MARKET_CAP = new HashMap<>();
        objFilters.put("CLOSE_PRICE", CLOSE_PRICE);
        objFilters.put("MARKET_CAP", MARKET_CAP);
        CLOSE_PRICE.put("min", 0);
        CLOSE_PRICE.put("max", 100000);
        MARKET_CAP.put("min", 0);
        MARKET_CAP.put("max", 3000000000000000l);
        int pageNo = 0;
        int companyCount = 0;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        StringBuilder builder = new StringBuilder();
        List<Map<String, Object>> sortedRecords = new ArrayList<>();
        String htmlTemplate = "";
        try {
            while (true) {
                payload.put("page", pageNo);
                pageNo++;
                payload.put("size", 1000);
                payload.put("sortBy", "NA");
                payload.put("sortType", "ASC");
                String jsonPayload = "";
                try {
                    jsonPayload = new ObjectMapper().writeValueAsString(payload);
                } catch (JsonProcessingException e) {
                }
                ResponseEntity<Map> response = template.postForEntity("https://groww.in/v1/api/stocks_data/v1/all_stocks", new HttpEntity<>(jsonPayload, httpHeaders), Map.class);
                List<Map<String, Object>> records = (List<Map<String, Object>>) response.getBody().get("records");
                if (records == null || records.isEmpty()) {
                    break;
                }
                for (Map<String, Object> record : records) {
                    Map<String, Object> livePriceDto = (Map<String, Object>) record.get("livePriceDto");
                    Map<String, Object> data = new HashMap<>();
                    data.put("companyName", record.get("companyName").toString().length() > 50 ? record.get("companyName").toString().substring(0, 50) : record.get("companyName"));
                    String dayChangePerc = livePriceDto.get("dayChangePerc").toString();
                    data.put("dayChangePerc", Double.valueOf(dayChangePerc));
                    data.put("ltp", livePriceDto.get("ltp"));
                    sortedRecords.add(data);
                    companyCount++;
                }
            }

            Collections.sort(sortedRecords, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> map1, Map<String, Object> map2) {
                    Double val1 = (Double) map1.get("dayChangePerc");
                    Double val2 = (Double) map2.get("dayChangePerc");
                    return Double.compare(val1, val2);
                }
            });

            for (Map<String, Object> sortedRec : sortedRecords) {
                builder.append("<tr>").append("<td>").append(sortedRec.get("companyName")).append("</td><td>").append(sortedRec.get("dayChangePerc")).append("</td><td>").append(sortedRec.get("ltp")).append("<td></tr>");
            }
            htmlTemplate = htmlHeder + builder.toString() + htmlFooter;
            if ("Y".equalsIgnoreCase(sent)) {
                JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
                javaMailSender.setHost("smtp.gmail.com");
                javaMailSender.setPort(587);
                javaMailSender.setUsername("portablemanagementsystems@gmail.com");
                javaMailSender.setPassword("ahwwyteyigkyixir");
                Properties properties = new Properties();
                properties.setProperty("mail.transport.protocol", "smtp");
                properties.setProperty("mail.smtp.auth", "true");
                properties.setProperty("mail.smtp.starttls.enable", "true");
                properties.setProperty("mail.debug", "false");
                javaMailSender.setJavaMailProperties(properties);
                MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = null;
                try {
                    helper = new MimeMessageHelper(mimeMessage, true);
                    System.out.println("companyCount: " + companyCount);

                    helper.setFrom("portabullmanagementsystems@gmail.com");
                    helper.setTo("harshagangavelli17@gmail.com");
                    helper.setSubject("Stock market Updates!!!!" + new Date());
                    helper.setText(htmlTemplate, true);
                    javaMailSender.send(mimeMessage);
                    System.out.println(htmlTemplate);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return htmlTemplate;
    }

}
