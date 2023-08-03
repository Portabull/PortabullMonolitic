package com.portabull.genericservice.service;

import com.portabull.payloads.EmailPayload;
import com.portabull.response.EmailResponse;
import com.portabull.response.PortableResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface GenericEmailService {

    public EmailResponse sendOTPToEmail(EmailPayload emailPayload);

    public EmailResponse sendEmail(List<MultipartFile> files, EmailPayload emailPayload) throws IOException;

    public PortableResponse sendOTPToMobile(String mobileNumber, boolean whatsApp);
}
