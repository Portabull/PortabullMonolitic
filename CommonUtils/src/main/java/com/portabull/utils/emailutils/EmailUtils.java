package com.portabull.utils.emailutils;

import com.portabull.constants.LoggerErrorConstants;
import com.portabull.constants.MessageConstants;
import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.payloads.EmailPayload;
import com.portabull.response.EmailResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;

@Component
public class EmailUtils {

    @Autowired
    JavaMailSender javaMailSender;

    private static final Logger logger = LoggerFactory.getLogger(EmailUtils.class);

    public EmailResponse sendEmail(EmailPayload payload) {
        logger.info(LoggerErrorConstants.SEND_MAIL_STARTS);
        EmailResponse response = new EmailResponse();
        try {

            validate(payload, response);
            if (response.hasErrors()) {
                return response;
            }

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            prepareEmailReceipts(payload, helper);

            prepareEmailSubjectAndBody(payload, helper);

            prepareAttachments(payload, helper);

            isValidEmailAddress(payload.getTo().get(0));

            javaMailSender.send(mimeMessage);

            prepareEmailResponse(PortableConstants.SUCCESS,
                    MessageConstants.EMAIL_SENT_SUCCESS, StatusCodes.C_200, response, false);

        } catch (MessagingException me) {
            logger.error(LoggerErrorConstants.EMAIL_SEND_ERROR, me);
            prepareEmailResponse(PortableConstants.FAILED,
                    MessageConstants.EMAIL_SENT_FAILED, StatusCodes.C_500, response, true);
        } catch (Exception e) {
            logger.error(LoggerErrorConstants.EMAIL_SEND_ERROR, e);
            prepareEmailResponse(PortableConstants.FAILED,
                    MessageConstants.EMAIL_SENT_FAILED, StatusCodes.C_500, response, true);
            if (!StringUtils.isEmpty(e.getMessage()) && e.getMessage().contains(PortableConstants.AUTHENTICATION_FAILED)) {
                prepareEmailResponse(PortableConstants.FAILED,
                        MessageConstants.AUTHENTICATION_FAILED, StatusCodes.C_401, response, true);
            }
        }
        logger.info(LoggerErrorConstants.SEND_MAIL_ENDS);
        return response;
    }

    private void prepareEmailResponse(String status, String message, Long statusCode, EmailResponse response,
                                      boolean hasErrors) {
        response.setStatus(status);
        response.setMessage(message);
        response.setStatusCode(statusCode);
        response.setHasErrors(hasErrors);
    }

    private void prepareEmailSubjectAndBody(EmailPayload payload, MimeMessageHelper helper) throws MessagingException {
        helper.setSubject(payload.getSubject());

        helper.setText(payload.getBody(), payload.isHtmlTemplete());

        helper.setSentDate(new Date());

    }

    private void prepareEmailReceipts(EmailPayload payload, MimeMessageHelper helper) throws MessagingException {
        helper.setFrom(payload.getFrom() != null ? payload.getFrom() : EmailConfiguration.getFrom());
        helper.setTo(payload.getTo().toArray(new String[payload.getTo().size()]));

        if (!CollectionUtils.isEmpty(payload.getCc())) {
            helper.setCc(payload.getCc().toArray(new String[payload.getCc().size()]));
        }

        if (!CollectionUtils.isEmpty(payload.getBcc())) {
            helper.setBcc(payload.getBcc().toArray(new String[payload.getBcc().size()]));
        }

    }

    private void prepareAttachments(EmailPayload payload, MimeMessageHelper helper) {
        payload.getAttachments().forEach(attachment -> {
            try {
                helper.addAttachment(attachment.getName(), new FileSystemResource(attachment));
            } catch (MessagingException e) {
                logger.error(LoggerErrorConstants.EMAIL_ATTACHEMENT_ERROR, e);
            }
        });
    }

    private void validate(EmailPayload payload, EmailResponse response) {
        if (CollectionUtils.isEmpty(payload.getTo())) {
            response.setMessage("Atleast One Email To Receipt is mandatory");
            response.setStatusCode(StatusCodes.C_400);
            response.setHasErrors(true);
        }

        if (StringUtils.isEmpty(payload.getFrom()) && StringUtils.isEmpty(EmailConfiguration.getFrom())) {
            response.setMessage("Email From should not be null");
            response.setStatusCode(StatusCodes.C_400);
            response.setHasErrors(true);
        }
    }

    public static EmailPayload prepareEmailPayload(EmailPayload emailPayload, String subject, String body, String message, boolean isHtmlTemplete) {
        if (emailPayload == null)
            emailPayload = new EmailPayload();

        emailPayload.setSubject(subject);
        emailPayload.setBody(body);
        emailPayload.setMessage(message);
        emailPayload.setHtmlTemplete(isHtmlTemplete);
        return emailPayload;
    }

    public static String getHtmlText(String fileName) {
        StringBuilder html = new StringBuilder();
        try {
            ClassPathResource cpr = new ClassPathResource(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(cpr.getInputStream()));
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

    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }


}
