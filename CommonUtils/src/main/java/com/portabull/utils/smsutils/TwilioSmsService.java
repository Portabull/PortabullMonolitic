package com.portabull.utils.smsutils;

import com.portabull.constants.PortableConstants;
import com.portabull.response.PortableResponse;
import com.portabull.response.SmsResponse;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Primary
public class TwilioSmsService implements SmsHelperUtils {

    static Logger logger = LoggerFactory.getLogger(TwilioSmsService.class);

    static PhoneNumber from;

    static PhoneNumber whatsAppFrom;

    public static void setPhoneNumber(PhoneNumber phoneNumber) {
        from = phoneNumber;
    }

    public static void setWhatsAppNumber(PhoneNumber phoneNumber) {
        whatsAppFrom = phoneNumber;
    }

    @Override
    public PortableResponse sendSMS(String message, String mobileNumber) {
        SmsResponse smsResponse = new SmsResponse();
        smsResponse.setMobileNumber(mobileNumber);
        try {
            PhoneNumber to = new PhoneNumber(mobileNumber);

            MessageCreator creator = Message.creator(to, from, message);

            creator.create();

            smsResponse.setMessage("Sms Sent Successfully");
            smsResponse.setStatus(PortableConstants.SUCCESS);
            smsResponse.setStatusCode(200L);
        } catch (ApiException ex) {
            logger.error("Exception:", ex);
            smsResponse.setMessage("Something went wrong please try after sometime");
            if (ex.getMessage() != null && ex.getMessage().contains("is not a valid phone number")) {
                smsResponse.setMessage("Phone number [" + mobileNumber + "] is not a valid number");
            }

            smsResponse.setStatus(PortableConstants.FAILED);
            smsResponse.setStatusCode(500L);
        } catch (Exception e) {
            logger.error("Exception:", e);
            smsResponse.setMessage("Something went wrong please try after sometime");
            smsResponse.setStatus(PortableConstants.FAILED);
            smsResponse.setStatusCode(500L);
        }
        return smsResponse;
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
        SmsResponse smsResponse = new SmsResponse();
        smsResponse.setMobileNumber(mobileNumber);
        try {
            PhoneNumber to = new PhoneNumber("whatsapp:" + mobileNumber);

            MessageCreator creator = Message.creator(to, whatsAppFrom, message);

            creator.create();

            smsResponse.setMessage("Sms Sent Successfully");
            smsResponse.setStatus(PortableConstants.SUCCESS);
            smsResponse.setStatusCode(200L);
        } catch (ApiException ex) {
            logger.error("Exception:", ex);
            smsResponse.setMessage("Something went wrong please try after sometime");
            if (ex.getMessage() != null && ex.getMessage().contains("is not a valid phone number")) {
                smsResponse.setMessage("Phone number [" + mobileNumber + "] is not a valid number");
            }

            smsResponse.setStatus(PortableConstants.FAILED);
            smsResponse.setStatusCode(500L);
        } catch (Exception e) {
            logger.error("Exception:", e);
            smsResponse.setMessage("Something went wrong please try after sometime");
            smsResponse.setStatus(PortableConstants.FAILED);
            smsResponse.setStatusCode(500L);
        }
        return smsResponse;
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
