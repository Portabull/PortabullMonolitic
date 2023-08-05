package com.portabull.oauth.services;

import com.portabull.generic.dao.CommonDao;
import com.portabull.generic.models.UserDocumentStorage;
import com.portabull.response.PortableResponse;
import com.portabull.um.UserCredentials;
import com.portabull.um.dao.UserCredentialsDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserOAuthServices {

    @Autowired
    CommonDao commonDao;

    @Autowired
    UserCredentialsDao userCredentialsDao;

    Logger logger = LoggerFactory.getLogger(UserOAuthServices.class);

    public UserCredentials getUserCredentials(String userName) {
        return userCredentialsDao.getUserCredentials(userName);
    }

    public PortableResponse registerIfUserNotExists(String userName) {

        PortableResponse registration = userCredentialsDao.registration(userName, null, userName, null, null, true);


        if (registration.getStatusCode() != 200)
            return registration.setData(null);

        if (!"Already User Registered".equalsIgnoreCase(registration.getMessage())) {
            UserDocumentStorage documentStorage = new UserDocumentStorage();
            documentStorage.setUserId((Long) registration.getData());
            documentStorage.setStorageSize(1000.0);
            documentStorage.setUserStorageSize(0.0);
            commonDao.saveOrUpdateEntity(documentStorage);
        }

        return registration.setData(null);
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
}
