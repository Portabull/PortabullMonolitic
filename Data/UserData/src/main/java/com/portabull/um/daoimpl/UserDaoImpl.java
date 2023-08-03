package com.portabull.um.daoimpl;

import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.dbutils.HibernateUtils;
import com.portabull.response.PortableResponse;
import com.portabull.um.UserCredentials;
import com.portabull.um.UserLoggedInDetails;
import com.portabull.um.UserProfile;
import com.portabull.um.dao.UserDao;
import com.portabull.utils.commonutils.CommonUtils;
import com.portabull.utils.dateutils.DateUtils;
import org.apache.commons.lang.BooleanUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserDaoImpl implements UserDao {


    @Autowired
    HibernateUtils hibernateUtils;

    @Override
    public PortableResponse getUsers(String userName, boolean retriveAllUsers) {
        try (Session session = hibernateUtils.getSession()) {
            if (retriveAllUsers) {
                List<List<Object>> users = session.createQuery(
                        "SELECT us.userID AS userId , us.loginUserName AS userName FROM UserCredentials us").list();

                return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, users);
            } else {
                List users = session.createQuery("SELECT us.userName,us.userID,us.loginUserName as u FROM UserCredentials us WHERE us.loginUserName =:userName")
                        .setParameter("userName",  userName).list();
                return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, users);
            }
        }
    }

    @Override
    public PortableResponse getUserReports() {
        PortableResponse response = new PortableResponse();
        try (Session session = hibernateUtils.getSession()) {
            Map<String, Object> customResponse = new HashMap<>();
            List<Map<String, Object>> internalEmails = new ArrayList<>();

            List<UserCredentials> userCredentials = session.createQuery(
                    "FROM UserCredentials WHERE (wrongPasswordCount IS NOT NULL AND wrongPasswordCount >= 3) OR (isAccountLocked IS NOT NULL AND isAccountLocked=true)").list();

            userCredentials.forEach(userCredential -> {
                Map<String, Object> internalEmail = new HashMap<>();
                internalEmail.put("senderAddress", userCredential.getLoginUserName());
                internalEmail.put("messageBody", userCredential.getReason());
                internalEmail.put("id", userCredential.getUserID());
                internalEmail.put("isLockedByAdmin", userCredential.getWrongPasswordCount() == null || userCredential.getWrongPasswordCount() < 3);
                internalEmails.add(internalEmail);
            });

            customResponse.put("internalEmails", internalEmails);

            UserCredentials loggedInuserCredentials = hibernateUtils.findEntity(UserCredentials.class, CommonUtils.getLoggedInUserId());
            customResponse.put("myAddress", loggedInuserCredentials.getLoginUserName());

            response.setStatus(PortableConstants.SUCCESS).setData(customResponse).setStatusCode(StatusCodes.C_200);
        }

        return response;
    }

    @Override
    public PortableResponse unlockUserAccount(Long userId) {
        Transaction transaction;
        try (Session session = hibernateUtils.getSession()) {
            transaction = session.beginTransaction();

            session.createQuery(
                    "UPDATE UserCredentials SET wrongPasswordCount=0,isAccountLocked=false WHERE userID=:userID").setParameter("userID", userId).executeUpdate();

            transaction.commit();
        }
        return new PortableResponse("Approved Successfully", StatusCodes.C_200, PortableConstants.SUCCESS, null);
    }

    @Override
    public PortableResponse lockUnlockAccount(String userName, String type) {
        PortableResponse response = new PortableResponse().setStatusCode(StatusCodes.C_200);
        try (Session session = hibernateUtils.getSession()) {
            Transaction transaction = session.beginTransaction();
            UserCredentials userCredentials = (UserCredentials) session.createQuery(
                    " FROM UserCredentials WHERE loginUserName=:userName").setParameter("userName",
                    userName).uniqueResult();

            if ("lock".equalsIgnoreCase(type)) {
                if (BooleanUtils.isTrue(userCredentials.getAccountLocked())) {
                    return response.setMessage("Account is already Locked");
                }
                userCredentials.setAccountLocked(true);
                response.setMessage("Account Locked Successfully");
            } else {
                if (BooleanUtils.isFalse(userCredentials.getAccountLocked())) {
                    return response.setMessage("Account is already UnLocked");
                }

                userCredentials.setAccountLocked(false);
                response.setMessage("Account UnLocked Successfully");
            }
            session.saveOrUpdate(userCredentials);
            transaction.commit();
        }
        return response;
    }

    @Override
    public PortableResponse userRegistered() {
        try (Session session = hibernateUtils.getSession()) {
            Boolean alreadyRegistered = (Boolean) session.createQuery("SELECT alreadyRegistered FROM UserCredentials WHERE userID=:userID")
                    .setParameter("userID", CommonUtils.getLoggedInUserId()).uniqueResult();

            return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, alreadyRegistered);

        }
    }

    @Override
    public PortableResponse registerUserData(Map<String, String> notifyDetails) {
        try (Session session = hibernateUtils.getSession()) {

            Transaction transaction = session.beginTransaction();

            UserProfile userProfile = (UserProfile) session.createQuery(
                            "SELECT up FROM UserProfile up INNER JOIN UserCredentials us ON (up.userCredentials.userID=us.userID) WHERE us.userID=:userID")
                    .setParameter("userID", CommonUtils.getLoggedInUserId()).uniqueResult();

            UserCredentials userCredentials = userProfile.getUserCredentials();

            userCredentials.setPassword(notifyDetails.get("password"));

            userCredentials.setAlreadyRegistered(true);

            userCredentials.setUserName(notifyDetails.get("firstName") + " " + notifyDetails.get("lastName"));

            userProfile.setMobileNumber(notifyDetails.get("phoneNumber"));

            session.saveOrUpdate(userCredentials);

            session.saveOrUpdate(userProfile);

            transaction.commit();

            notifyDetails.get("date");
            notifyDetails.get("month");
            notifyDetails.get("year");

            return new PortableResponse("User Data Registered", StatusCodes.C_200, PortableConstants.SUCCESS, null);

        }
    }

    @Override
    public PortableResponse lockUnlockCompleteUsers(Integer flag) {
        try (Session session = hibernateUtils.getSession()) {
            Query query;
            Transaction transaction = session.beginTransaction();
            if (flag == 0) {
                query = session.createQuery("UPDATE UserCredentials SET isAccountLocked=false WHERE (isAdmin IS NULL OR isAdmin=false)");
            } else {
                query = session.createQuery("UPDATE UserCredentials SET isAccountLocked=true WHERE (isAdmin IS NULL OR isAdmin=false)");
            }
            query.executeUpdate();
            transaction.commit();
        }
        return new PortableResponse(flag == 0 ? "Unlocked Successfully" : "Locked Successfully", StatusCodes.C_200, PortableConstants.SUCCESS, null);
    }

    @Override
    public PortableResponse getLastLoggedTime() {
        try (Session session = hibernateUtils.getSession()) {
            UserLoggedInDetails details = (UserLoggedInDetails) session.createQuery(" FROM UserLoggedInDetails where userId=:userId order by loggedInDate DESC")
                    .setParameter("userId", CommonUtils.getLoggedInUserId())
                    .setFirstResult(1)
                    .setMaxResults(1).uniqueResult();

            if (details != null) {
                return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, DateUtils.formatDate(DateUtils.PORTABLE_DEFAULT_DATE, details.getLoggedInDate()));
            }
        }
        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, "");
    }

}
