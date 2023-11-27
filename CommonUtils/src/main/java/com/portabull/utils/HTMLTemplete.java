package com.portabull.utils;

import com.portabull.constants.PortableConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class HTMLTemplete {

    static Logger logger = LoggerFactory.getLogger(HTMLTemplete.class);

    public String getOTPEmailTemplete(String otp, int expiryTimeInMins) {
        return getHtmlText("otpEmailTemplete.html").replace(
                "{ONE_TIME_PASSWORD}", otp
        ).replace(
                "{ONE_TIME_PASSWORD_TIME}",
                String.valueOf(
                        expiryTimeInMins
                )
        );
    }

    public String getNotifyEmailTemplate(String token) {

        String location = RequestHelper.getCurrentRequest().getHeader("location");

        String loggedDetails = getLoggedInDetails(location);

        String latLong = PortableConstants.MAPS_URL;
        if (!StringUtils.isEmpty(location) && !"null".equalsIgnoreCase(location)) {
            latLong = latLong + location.split(";")[0] + "," + location.split(";")[1];
        }

        return getHtmlText("appovalRequest.html").replace(
                        "{DEVICE_DETAILS}", loggedDetails)
                .replace("{CLICK_HERE_TO_SEE_DETAILS_IN_MAP}", latLong)
                .replace("{PORTABULL_VERIFY_URL_APPROVE}", "https://portabull.in/APIGateway/approve-request?token=" + token + "&approval=1")
                .replace("{PORTABULL_VERIFY_URL_DECLINE}", "https://portabull.in/APIGateway/approve-request?token=" + token + "&approval=2");
    }


    public static String getHtmlText(String fileName) {
        StringBuilder html = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new ClassPathResource(fileName).getInputStream()));
            String val;
            while ((val = br.readLine()) != null) {
                html.append(val);
            }
            String result = html.toString();
            logger.info(result);
            br.close();
        } catch (Exception ex) {
            logger.error("While preparing html text it throws error", ex);
        }
        return html.toString();
    }

    public String getLoggedInDetails(String location) {

        StringBuilder loggedUserDetails = new StringBuilder("<p>Device Details : ");

        String userAgent = RequestHelper.getCurrentRequest().getHeader("user-agent");

        loggedUserDetails.append(userAgent).append("</p><br><p>Location Details : ");

        String latitude = null;
        String longitute = null;

        if (!StringUtils.isEmpty(location) && !"null".equalsIgnoreCase(location)) {
            latitude = location.split(";")[0];
            longitute = location.split(";")[1];
        }

        if (latitude != null && longitute != null) {
            prepareLocationDetails(latitude, longitute, loggedUserDetails);
        } else {
            loggedUserDetails.append("No Location Details Found, Please make sure whose logged in</p>");
        }

        return loggedUserDetails.toString();
    }

    private static void prepareLocationDetails(String latitude, String longitute, StringBuilder loggedUserDetails) {
        StringBuilder url = new StringBuilder("https://api.bigdatacloud.net/data/reverse-geocode-client");

        url.append("?latitude=").append(latitude).append("&longitude=").append(longitute).append("&localityLanguage=en");

        logger.info("Url : " + url);

        Map<String, Object> data = new RestTemplate().getForObject(url.toString(), Map.class);

        String city = data.get("city").toString();

        loggedUserDetails.append("City : ").append(city).append(" ");

        String countryName = data.get("countryName").toString();

        loggedUserDetails.append("Country : ").append(countryName).append(" ");

        String state = data.get("principalSubdivision").toString();


        loggedUserDetails.append("State : ").append(state).append(" ");

        String locality = data.get("locality").toString();

        loggedUserDetails.append("Locality : ").append(locality).append(" ");

        LinkedHashMap localityInfo = (LinkedHashMap) data.get("localityInfo");

        List<LinkedHashMap> adminData = (List<LinkedHashMap>) localityInfo.get("administrative");

        String address = "";

        for (int i = adminData.size() - 1; i >= 0; i--) {
            address = address + ", " + adminData.get(i).get("name");
        }


        loggedUserDetails.append("Address : ").append(address).append("</p>");

        logger.info(loggedUserDetails.toString());

    }

}
