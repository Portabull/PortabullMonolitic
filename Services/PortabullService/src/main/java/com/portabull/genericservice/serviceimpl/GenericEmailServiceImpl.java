package com.portabull.genericservice.serviceimpl;

import com.portabull.genericservice.service.GenericEmailService;
import com.portabull.genericservice.service.GenericOTPService;
import com.portabull.payloads.EmailPayload;
import com.portabull.payloads.OTPPayload;
import com.portabull.response.DocumentResponse;
import com.portabull.response.EmailResponse;
import com.portabull.response.PortableResponse;
import com.portabull.utils.HTMLTemplete;
import com.portabull.utils.emailutils.EmailUtils;
import com.portabull.utils.fileutils.FileHandling;
import com.portabull.utils.smsutils.SmsHelperUtils;
import com.portabull.utils.validationutils.FileValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GenericEmailServiceImpl implements GenericEmailService {

    @Autowired
    EmailUtils emailUtils;

    @Autowired
    GenericOTPService otpService;

    @Autowired
    HTMLTemplete htmlTemplete;

    @Autowired
    @Qualifier("invokeSmsService")
    SmsHelperUtils smsHelperUtils;

    static Logger logger = LoggerFactory.getLogger(GenericEmailServiceImpl.class);

    @Override
    public EmailResponse sendOTPToEmail(EmailPayload emailPayload) {
        OTPPayload otpPayload = otpService.generateOTP();

        String otpEmailTemplete = htmlTemplete.getOTPEmailTemplete(otpPayload.getOtp(), 10);

        emailPayload.setSubject("Generated OTP from Portabull");
        emailPayload.setBody(otpEmailTemplete);
        emailPayload.setHtmlTemplete(true);

        otpPayload.setOtp(null);
        return emailUtils.sendEmail(emailPayload).setData(otpPayload);
    }

    @Override
    public EmailResponse sendEmail(List<MultipartFile> files, EmailPayload emailPayload) throws IOException {
        List<MultipartFile> validFiles;
        EmailResponse emailResponse;

        if (StringUtils.isEmpty(emailPayload.getSubject())) {
            return new EmailResponse().setMessage("Email Subject Should not be empty");
        }

        if (StringUtils.isEmpty(emailPayload.getBody())) {
            return new EmailResponse().setMessage("Email Body Should not be empty");
        }

        if (!CollectionUtils.isEmpty(files)) {
            validFiles = new ArrayList<>();
            files.forEach(file -> {
                DocumentResponse validationResponse = FileValidationUtils.validate(file);
                if (!validationResponse.containErrors()) {
                    validFiles.add(file);
                }
            });

            if (!CollectionUtils.isEmpty(validFiles)) {
                for (MultipartFile validFile : validFiles) {
                    emailPayload.setAttachment(FileHandling.convertMultipartToFile(validFile));
                }
            }
        }

        emailResponse = emailUtils.sendEmail(emailPayload);

        new Thread(() -> deleteTempFiles(emailPayload)).start();

        return emailResponse;
    }

    @Override
    public PortableResponse sendOTPToMobile(String mobileNumber, boolean whatsApp) {
        OTPPayload otpPayload = otpService.generateOTP();

        String message = "P-" + otpPayload.getOtp() + " is your Portabull verification code";


        return whatsApp ? smsHelperUtils.sendMessageToWhatsApp(message, mobileNumber) : smsHelperUtils.sendSMS(message, mobileNumber);
    }

    private void deleteTempFiles(EmailPayload emailPayload) {
        emailPayload.getAttachments().forEach(attachment -> {
            try {
                FileHandling.deleteFile(attachment);
                logger.info("Temp File deleted successfully");
            } catch (IOException ioException) {
                logger.error("Unable to delete the file:", ioException);
            }
        });
    }
}
