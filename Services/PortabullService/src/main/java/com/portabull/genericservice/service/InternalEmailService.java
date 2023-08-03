package com.portabull.genericservice.service;

import com.portabull.response.PortableResponse;

import java.util.List;

public interface InternalEmailService {

    public PortableResponse sendInternalEmail(String subject, String body, Long userId);

    public PortableResponse getInternalEmails(String emailType);

    public PortableResponse updateMailSeen(List<Long> mailIds);
}
