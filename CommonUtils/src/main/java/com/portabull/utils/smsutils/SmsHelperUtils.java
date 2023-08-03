package com.portabull.utils.smsutils;

import com.portabull.response.PortableResponse;

import java.util.List;

public interface SmsHelperUtils {

    public PortableResponse sendSMS(String message, String mobileNumber);

    public List<PortableResponse> sendSMS(String message, List<String> mobileNumbers);

    public PortableResponse sendMessageToWhatsApp(String message, String mobileNumber);

    public List<PortableResponse> sendMessageToWhatsApp(String message, List<String> mobileNumbers);

}
