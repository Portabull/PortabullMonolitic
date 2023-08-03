package com.portabull.generic.daoimpl;

import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.dbutils.HibernateUtils;
import com.portabull.generic.dao.InternalEmailDao;
import com.portabull.generic.models.InternalEmails;
import com.portabull.response.PortableResponse;
import com.portabull.um.UserCredentials;
import com.portabull.utils.commonutils.CommonUtils;
import com.portabull.utils.dateutils.DateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InternalEmailDaoImpl implements InternalEmailDao {

    @Autowired
    HibernateUtils hibernateUtils;

    @Override
    public PortableResponse sendInternalEmail(String subject, String body, Long userId) {
        Long loggedInUser = CommonUtils.getLoggedInUserId();
        InternalEmails email = new InternalEmails();
        email.setMessageSubject(subject);
        email.setSenderId(loggedInUser);
        email.setMessageBody(body);
        email.setReceiverId(userId);
        email.setCreatedDate(DateUtils.getDefaultTime());
        email.setMailSeen(false);

        UserCredentials receiverCredentials = hibernateUtils.findEntity(UserCredentials.class, userId);
        email.setReceiverAddress(receiverCredentials.getLoginUserName());

        UserCredentials senderCredentials = hibernateUtils.findEntity(UserCredentials.class, loggedInUser);
        email.setSenderAddress(senderCredentials.getLoginUserName());

        hibernateUtils.saveOrUpdateEntity(email);
        return new PortableResponse("Email Send Successfully", StatusCodes.C_200, PortableConstants.FAILED, null);
    }

    @Override
    public PortableResponse getInternalEmails(String emailType) {
        PortableResponse portableResponse = new PortableResponse();
        try (Session session = hibernateUtils.getSession()) {
            Map<String, Object> response = new HashMap<>();
            List<InternalEmails> internalEmails;
            Long loggedInUser = CommonUtils.getLoggedInUserId();
            if ("INBOX".equalsIgnoreCase(emailType)) {
                internalEmails = session.createQuery(" FROM InternalEmails WHERE receiverId=:receiverId ORDER BY messageId DESC").setParameter("receiverId", loggedInUser).list();
            } else {
                internalEmails = session.createQuery(" FROM InternalEmails WHERE senderId=:senderId ORDER BY messageId DESC").setParameter("senderId", loggedInUser).list();
            }
            response.put("internalEmails", internalEmails);
            UserCredentials userCredentials = hibernateUtils.findEntity(UserCredentials.class, loggedInUser);
            response.put("myAddress", userCredentials.getLoginUserName());
            return portableResponse.setData(response);
        }

    }

    @Override
    public PortableResponse updateMailSeen(List<Long> mailIds) {
        try (Session session = hibernateUtils.getSession()) {
            Transaction transaction = session.beginTransaction();
            session.createQuery(" UPDATE InternalEmails set mailSeen=true WHERE messageId IN (:mailIds)")
                    .setParameter("mailIds", mailIds).executeUpdate();
            transaction.commit();
            return new PortableResponse("Updated SUccessfully", StatusCodes.C_200, PortableConstants.SUCCESS, null);
        }
    }

}
