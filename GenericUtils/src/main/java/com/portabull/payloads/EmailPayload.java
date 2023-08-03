package com.portabull.payloads;

import com.portabull.constants.PortableConstants;
import com.portabull.response.EmailResponse;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EmailPayload implements Serializable {

    private String from;

    private List<String> to;

    private List<String> cc;

    private List<String> bcc;

    private String subject;

    private String body;

    private List<File> attachments = new ArrayList<>();

    private List<String> completeFilePaths = new ArrayList<>();

    private String message;

    private boolean isHtmlTemplete;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public List<String> getCc() {
        return cc;
    }

    public void setCc(List<String> cc) {
        this.cc = cc;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<File> getAttachments() {
        return attachments;
    }

    public EmailPayload setAttachment(File attachment) {
        if (!attachment.exists())
            return this;

        this.attachments.add(attachment);
        return this;
    }

    public EmailPayload setAttachments(List<File> attachments) {
        if (!CollectionUtils.isEmpty(attachments)) {
            attachments.forEach(attachment -> {
                if (attachment.exists())
                    this.attachments.add(attachment);
            });
        }
        return this;
    }

    public boolean isHtmlTemplete() {
        return isHtmlTemplete;
    }

    public void setHtmlTemplete(boolean htmlTemplete) {
        isHtmlTemplete = htmlTemplete;
    }


    public List<String> getBcc() {
        return bcc;
    }

    public void setBcc(List<String> bcc) {
        this.bcc = bcc;
    }

    public List<String> getCompleteFilePaths() {
        return completeFilePaths;
    }

    public void setCompleteFilePaths(List<String> completeFilePaths) {
        if (!CollectionUtils.isEmpty(completeFilePaths)) {
            completeFilePaths.forEach(completePath -> {
                if (new File(completePath).exists()) {
                    this.completeFilePaths.add(completePath);
                    this.attachments.add(new File(completePath));
                }
            });
        }
    }

    public EmailResponse validateEmailPayload(EmailPayload emailPayload) {
        EmailResponse emailResponse = new EmailResponse();
        if (emailPayload == null || CollectionUtils.isEmpty(emailPayload.getTo())) {
            emailResponse.setMessage("Email To is mandatory");
            emailResponse.setHasErrors(true);
            emailResponse.setStatus(PortableConstants.FAILED);
            emailResponse.setStatusCode(400L);
            return emailResponse;
        }
        emailResponse.setHasErrors(false);
        return emailResponse;
    }


}
