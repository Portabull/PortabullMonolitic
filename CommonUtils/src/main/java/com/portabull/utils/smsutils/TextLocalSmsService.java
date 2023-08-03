package com.portabull.utils.smsutils;

import com.portabull.response.PortableResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TextLocalSmsService implements SmsHelperUtils {

    static Logger logger = LoggerFactory.getLogger(TextLocalSmsService.class);

    @Override
    public PortableResponse sendSMS(String message, String mobileNumber) {
        logger.info("sendSMS() : starts");
        return null;
    }

    @Override
    public List<PortableResponse> sendSMS(String message, List<String> mobileNumbers) {
        List<PortableResponse> responses = new ArrayList<>();
        mobileNumbers.forEach(mobileNumber ->
                responses.add(sendSMS(message, mobileNumber))
        );
        return responses;
    }

    @Override
    public PortableResponse sendMessageToWhatsApp(String message, String mobileNumber) {
        logger.info("sendMessageToWhatsApp() : starts");
        return null;
    }

    @Override
    public List<PortableResponse> sendMessageToWhatsApp(String message, List<String> mobileNumbers) {
        List<PortableResponse> responses = new ArrayList<>();
        mobileNumbers.forEach(mobileNumber ->
                responses.add(sendMessageToWhatsApp(message, mobileNumber))
        );
        return responses;
    }

}
