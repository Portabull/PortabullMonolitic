package com.portabull.genericservice.serviceimpl;

import com.portabull.generic.dao.InternalEmailDao;
import com.portabull.genericservice.service.InternalEmailService;
import com.portabull.response.PortableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InternalEmailServiceImpl implements InternalEmailService {

    @Autowired
    InternalEmailDao emailDao;


    @Override
    public PortableResponse sendInternalEmail(String subject, String body, Long userId) {
        return emailDao.sendInternalEmail(subject, body, userId);
    }

    @Override
    public PortableResponse getInternalEmails(String emailType) {
        return emailDao.getInternalEmails(emailType);
    }

    @Override
    public PortableResponse updateMailSeen(List<Long> mailIds) {
        return emailDao.updateMailSeen(mailIds);
    }

}
