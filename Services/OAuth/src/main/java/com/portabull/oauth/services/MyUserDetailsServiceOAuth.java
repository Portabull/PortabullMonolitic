package com.portabull.oauth.services;

import com.portabull.constants.PortableConstants;
import com.portabull.generic.dao.CommonDao;
import com.portabull.generic.models.UserDocumentStorage;
import com.portabull.response.PortableResponse;
import com.portabull.um.UserCredentials;
import com.portabull.um.UserProfile;
import com.portabull.um.dao.UserCredentialsDao;
import com.portabull.um.dao.UserDao;
import com.portabull.utils.validationutils.Validations;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MyUserDetailsServiceOAuth implements UserDetailsService {

    @Autowired
    UserCredentialsDao userCredentialsDao;

    @Autowired
    CommonDao commonDao;

    @Autowired
    UserDao userDao;

    static Logger logger = LoggerFactory.getLogger(MyUserDetailsServiceOAuth.class);

    @Override
    public UserDetails loadUserByUsername(String validUserName) throws UsernameNotFoundException {
        String password = "";
        if (StringUtils.isEmpty(validUserName)) {
            return new User(validUserName, password, new ArrayList<>());
        }

        UserCredentials userCredential = userCredentialsDao.getUserCredentials(validUserName);

        if (!Validations.isEmptyObject(userCredential)) {

            if (BooleanUtils.isTrue(userCredential.getAccountLocked())) {
                return new User(validUserName, password, false, true, true, true, new ArrayList<>());
            }

            if (userCredential.getWrongPasswordCount() != null && userCredential.getWrongPasswordCount() >= 3) {
                password = userCredential.getPassword();
                return new User(validUserName, password, false, true, true, false, new ArrayList<>());
            }

            password = userCredential.getPassword();

        }

        return new User(validUserName, password, new ArrayList<>());
    }

    public boolean updateWrongPasswordCount(UserCredentials userCredentials, boolean isValidUser) {
        boolean isInvalidPassword = false;
        try {
            isInvalidPassword = userCredentialsDao.updateWrongPasswordCount(userCredentials, isValidUser);
        } catch (Exception e) {
            logger.error("While updating wrong password count it throws error", e);
        }
        return isInvalidPassword;
    }

    public UserCredentials getUserCredentials(String userName) {
        return userCredentialsDao.getUserCredentials(userName);
    }


    public UserProfile getUserProfiles(Long userID) {
        return userCredentialsDao.getUserProfiles(userID);
    }

    public boolean isAlreadyUserExists(String userName, String password, String email) {
        return userCredentialsDao.isAlreadyUserExists(userName, password, email);
    }

    public PortableResponse registration(String userName, String password, String email, String otp, String token) {
        PortableResponse registration = userCredentialsDao.registration(userName, password, email, otp, token, false);
        if (registration.getStatusCode() != 200)
            return registration.setData(null);

        UserDocumentStorage documentStorage = new UserDocumentStorage();
        documentStorage.setUserId((Long) registration.getData());
        documentStorage.setStorageSize(1000.0);
        documentStorage.setUserStorageSize(0.0);
        commonDao.saveOrUpdateEntity(documentStorage);
        return registration.setData(null);
    }


    public PortableResponse updatePasswordDetails(Map<String, String> passwordDetails) {
        if (!passwordDetails.get("newPassword").equals(passwordDetails.get("confirmNewPassword"))) {
            return new PortableResponse("Password and confrim password does not match", 400L, PortableConstants.FAILED, null);
        }

        UserCredentials userCredentials = userCredentialsDao.getUserCredentials(passwordDetails.get("to"));

        Map<String, Object> requestParams = new LinkedHashMap<>();

        requestParams.put("userId", userCredentials.getUserID());

        requestParams.put("otp", passwordDetails.get("otp"));

        List<Object> otps = commonDao.execueQuery(" FROM LoggedInOTP WHERE userId=:userId AND otp=:otp", requestParams);

        if (!CollectionUtils.isEmpty(otps)) {
            commonDao.deleteEntitys(otps);

            userCredentials.setPassword(passwordDetails.get("newPassword"));

            commonDao.saveOrUpdateEntity(userCredentials);

            return new PortableResponse("Successfully updated password", 200L, PortableConstants.SUCCESS, null);
        }

        return new PortableResponse("Invalid Otp", 401L, PortableConstants.FAILED, null);
    }

    public PortableResponse notifyAdministrator(Map<String, String> notifyDetails) {
        return userCredentialsDao.notifyAdministrator(notifyDetails.get("emailAddress"), notifyDetails.get("reason"));
    }

    public PortableResponse getUsers(String userName) {

        return userDao.getUsers(userName, false);

    }

    public PortableResponse getUserReports() {
        return userDao.getUserReports();
    }

    public PortableResponse unlockUserAccount(Object id) {
        if (id == null || Long.valueOf(id.toString()) == 0) {

        }

        return userDao.unlockUserAccount(Long.valueOf(id.toString()));
    }

    public PortableResponse lockUnlockAccount(Object serviceType, Object type) {
        if (StringUtils.isEmpty(serviceType)) {

        }

        return userDao.lockUnlockAccount(serviceType.toString(), type.toString());
    }
}
