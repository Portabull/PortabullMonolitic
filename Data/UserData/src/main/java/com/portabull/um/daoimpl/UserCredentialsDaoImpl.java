package com.portabull.um.daoimpl;

import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.dbutils.HibernateUtils;
import com.portabull.response.PortableResponse;
import com.portabull.um.RegistrationOtps;
import com.portabull.um.UserCredentials;
import com.portabull.um.UserLoggedInDetails;
import com.portabull.um.UserProfile;
import com.portabull.um.dao.UserCredentialsDao;
import com.portabull.utils.RequestHelper;
import com.portabull.utils.commonutils.CommonUtils;
import com.portabull.utils.validationutils.Validations;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.*;

@Repository
public class UserCredentialsDaoImpl implements UserCredentialsDao {

    @Autowired
    HibernateUtils hibernateUtils;

    private static Logger logger = LoggerFactory.getLogger(UserCredentialsDaoImpl.class);

    @Override
    public UserCredentials getUserCredential() {
        try (Session session = hibernateUtils.getSession()) {
            return (UserCredentials) session.createQuery(" FROM UserCredentials WHERE userID=:userID")
                    .setParameter("userID", CommonUtils.getLoggedInUserId()).uniqueResult();
        }
    }

    @Override
    public UserCredentials getUserCredentials(String userName) {
        UserCredentials userCredential = null;
        try (Session session = hibernateUtils.getSession()) {
            userCredential = (UserCredentials) session.createQuery(" FROM UserCredentials where loginUserName =:userName ")
                    .setParameter("userName", userName).uniqueResult();
        } catch (Exception e) {
            logger.error("While fetching UserCredentials hrows error", e);
        }
        return userCredential;
    }

    @Override
    public UserCredentials getUserCredential(String userName, String password) {
        return null;
    }

    @Override
    public boolean updateWrongPasswordCount(UserCredentials userCredential, boolean isValidUser) throws Exception {
        boolean isInvalidPassword = false;
        UserCredentials userCredentials = Validations.isEmptyObject(userCredential.getUserID()) ?
                this.getUserCredentials(userCredential.getLoginUserName())
                : userCredential;

        if (Validations.isEmptyObject(userCredentials))
            return false;

        updateLatLongDetails(userCredentials, isValidUser);

        if (isValidUser) {
            if (userCredentials.getWrongPasswordCount() == null || userCredentials.getWrongPasswordCount() == 0) {
                return true;
            }
            userCredentials.setWrongPasswordCount(0);
        } else {
            userCredentials.setWrongPasswordCount(userCredentials.getWrongPasswordCount() != null ? userCredentials.getWrongPasswordCount() + 1 : 1);
            userCredentials.setAccountLocked(userCredentials.getWrongPasswordCount() >= 3 ? true : false);
        }

        hibernateUtils.saveOrUpdateEntity(userCredentials);

        return isInvalidPassword;
    }

    private void updateLatLongDetails(UserCredentials userCredentials, boolean isValidUser) {
        String latitude = null;
        String longitute = null;
        String location = RequestHelper.getCurrentRequest().getHeader("location");
        if (!StringUtils.isEmpty(location) && !"null".equalsIgnoreCase(location)) {
            latitude = location.split(";")[0];
            longitute = location.split(";")[1];
        }

        try (Session session = hibernateUtils.getSession()) {
            if (((Long) session.createQuery("select COUNT(*) FROM UserLoggedInDetails where userId=:userId")
                    .setParameter("userId", userCredentials.getUserID()).uniqueResult()) < userCredentials.getNoOfLastLoggedInDetailsCap()) {
                hibernateUtils.saveOrUpdateEntity(new UserLoggedInDetails(userCredentials.getUserID(),
                        latitude, longitute, isValidUser, new Date()));
            } else {
                UserLoggedInDetails userLoggedInDetails = (UserLoggedInDetails) session.createQuery(" FROM UserLoggedInDetails where userId=:userId order by loggedInDate")
                        .setParameter("userId", userCredentials.getUserID())
                        .setFirstResult(1)
                        .setMaxResults(1).uniqueResult();
                userLoggedInDetails.setLoggedInDate(new Date());
                userLoggedInDetails.setLattitude(latitude);
                userLoggedInDetails.setLongitude(longitute);
                userLoggedInDetails.setSuccessfullyLoggedIn(isValidUser);
                hibernateUtils.saveOrUpdateEntity(userLoggedInDetails);
            }
        }
    }

    @Override
    public UserProfile getUserProfiles(Long userID) {
        UserProfile userProfile = null;
        try (Session session = hibernateUtils.getSession()) {
            userProfile = (UserProfile) session.createQuery(" FROM UserProfile where userCredentials.userID =:userID ")
                    .setParameter("userID", userID).uniqueResult();
        } catch (Exception e) {
            logger.error("While fetching UserCredentials hrows error", e);
        }
        return userProfile;
    }

    @Override
    public boolean isAlreadyUserExists(String userName, String password, String email) {
        try (Session session = hibernateUtils.getSession()) {
            UserCredentials userCredentials =
                    (UserCredentials) session.createQuery(" FROM UserCredentials where loginUserName=:userName").setParameter("userName", email).uniqueResult();
            return userCredentials != null;
        }
    }

    @Override
    public PortableResponse registration(String userName, String password, String email, String otp, String token, boolean isLoggedWithOAuth) {

        if (!isLoggedWithOAuth) {
            PortableResponse response = validateRegistrationOtp(token, otp);
            if (response.getStatusCode() != 200) {
                return response;
            }
        } else {
            if (isAlreadyUserRegistered(email)) {
                return new PortableResponse("Already User Registered", StatusCodes.C_200, PortableConstants.SUCCESS, null);
            }
        }

        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setLoginUserName(email);
        userCredentials.setPassword(password);
        userCredentials.setWrongPasswordCount(0);
        userCredentials.setAccountLocked(false);
        userCredentials.setNoOfLastLoggedInDetailsCap(10L);
        userCredentials.setLoggedInSessionTime(10L);
        userCredentials.setLoggedWithOAuth(isLoggedWithOAuth);
        userCredentials.setAlreadyRegistered(false);
        userCredentials.setUserName(userName);

        hibernateUtils.saveOrUpdateEntity(userCredentials);

        UserProfile userProfile = new UserProfile();
        userProfile.setUserCredentials(userCredentials);
        hibernateUtils.saveOrUpdateEntityList(Arrays.asList(userProfile));

        return new PortableResponse().setMessage("Registartion Done").setStatusCode(200L).setData(userCredentials.getUserID());
    }

    private boolean isAlreadyUserRegistered(String email) {
        try (Session session = hibernateUtils.getSession()) {
            String userName = (String) session.createQuery(
                            "SELECT loginUserName FROM UserCredentials WHERE loginUserName=:userName")
                    .setParameter("userName", email).uniqueResult();
            return userName != null;
        }
    }

    @Override
    public PortableResponse validateRegistrationOtp(String authorizationToken, String otp) {
        RegistrationOtps registrationOtps = null;
        try (Session session = hibernateUtils.getSession()) {
            registrationOtps = (RegistrationOtps) session.createQuery(
                            " FROM RegistrationOtps WHERE token=:token")
                    .setParameter("token", authorizationToken).uniqueResult();
        }
        if (registrationOtps != null) {
            if (new Date().getTime() > registrationOtps.getOtpExpiredDate().getTime()) {
                hibernateUtils.deleteEntitys(Arrays.asList(registrationOtps));
                return new PortableResponse().setMessage("Otp Expired").setStatusCode(401L);
            }

            if (registrationOtps.getOtp().equals(otp)) {
                hibernateUtils.deleteEntitys(Arrays.asList(registrationOtps));
                return new PortableResponse().setMessage("Valid Otp").setStatusCode(200L);
            }
            return new PortableResponse().setMessage("Invalid Otp").setStatusCode(401L);
        }
        return new PortableResponse().setMessage("UnAuthorized Access").setStatusCode(401L);
    }

    @Override
    public PortableResponse notifyAdministrator(String emailAddress, String reason) {
        try (Session session = hibernateUtils.getSession()) {
            UserCredentials userCredentials = (UserCredentials) session.createQuery(
                            " FROM UserCredentials WHERE loginUserName=:userName")
                    .setParameter("userName", emailAddress).uniqueResult();

            if (userCredentials == null)
                return new PortableResponse("No User Found", 401L, PortableConstants.FAILED, null);

            userCredentials.setReason(reason);
            hibernateUtils.saveOrUpdateEntity(session, userCredentials);
            return new PortableResponse(
                    "Notified Administrator successfully You will get an email/sms once your account is unlocked",
                    200L, PortableConstants.SUCCESS, null);
        }
    }

    @Override
    public UserCredentials saveOrUpdateUserCredentials(UserCredentials userCredentials) {
        return hibernateUtils.saveOrUpdateEntity(userCredentials);
    }

    @Override
    public PortableResponse getUserNames(List<Object> userIds) {

        Map<Long, String> response = new HashMap<>();

        List<Object[]> userResponse;

        List<Long> tempUserIDs = new ArrayList<>();

        for (int i = 0; i < userIds.size(); i++) {
            tempUserIDs.add(Long.valueOf(userIds.get(i).toString()));
        }

        try (Session session = hibernateUtils.getSession()) {

            userResponse = session.createQuery(
                            "SELECT userID,loginUserName FROM UserCredentials WHERE userID IN (:userIDS)")
                    .setParameter("userIDS", tempUserIDs).list();

        }

        userResponse.forEach(user -> response.put(Long.valueOf(user[0].toString()), user[1].toString()));

        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, response);

    }

    @Override
    public PortableResponse getUserIds(List<String> userNames) {

        Map<String, String> response = new HashMap<>();

        List<Object[]> userResponse;

        try (Session session = hibernateUtils.getSession()) {

            userResponse = session.createQuery(
                            "SELECT loginUserName,userID FROM UserCredentials WHERE loginUserName IN (:loginUserNames)")
                    .setParameter("loginUserNames", userNames).list();

        }

        userResponse.forEach(user -> response.put(user[0].toString(), user[1].toString()));

        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, response);

    }

    @Override
    public UserProfile saveOrUpdateUserProfile(UserProfile userProfile) {
        return hibernateUtils.saveOrUpdateEntity(userProfile);
    }


}
