package com.portabull.oauth.service;

import com.portabull.constants.LoggerErrorConstants;
import com.portabull.constants.PortableConstants;
import com.portabull.dms.service.DocumentService;
import com.portabull.generic.dao.CommonDao;
import com.portabull.generic.models.UserDocumentStorage;
import com.portabull.response.DocumentResponse;
import com.portabull.response.PortableResponse;
import com.portabull.um.UserCredentials;
import com.portabull.um.dao.UserCredentialsDao;
import com.portabull.utils.MockMultipartFile;
import com.portabull.utils.fileutils.FileHandling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLConnection;

@Service
public class UserOAuthServices {


    static RestTemplate template = new RestTemplate();

    @Autowired
    CommonDao commonDao;

    @Autowired
    UserCredentialsDao userCredentialsDao;

    @Autowired
    DocumentService documentService;

    Logger logger = LoggerFactory.getLogger(UserOAuthServices.class);

    public UserCredentials getUserCredentials(String userName) {
        return userCredentialsDao.getUserCredentials(userName);
    }

    public PortableResponse registerIfUserNotExists(DefaultOidcUser user) {

        String userName = user.getAttributes().get("email").toString();

        PortableResponse registration = userCredentialsDao.registration(userName, null, userName, null, null, true);

        registration.setData(null);

        if (!"Already User Registered".equalsIgnoreCase(registration.getMessage())) {
            UserDocumentStorage documentStorage = new UserDocumentStorage();
            documentStorage.setUserId((Long) registration.getData());
            documentStorage.setStorageSize(1000.0);
            documentStorage.setUserStorageSize(0.0);
            commonDao.saveOrUpdateEntity(documentStorage);
            registration.setData(documentStorage);
        }

        return registration;
    }

    public void uploadUserProfilePicture(DefaultOidcUser user, UserDocumentStorage documentStorage, String userName) {

        try {

            if (documentStorage == null || StringUtils.isEmpty(user.getAttributes().get("picture"))) {
                return;
            }


            String profilePicUrl = user.getAttributes().get("picture").toString();

            byte[] bytes = template.getForObject(profilePicUrl, byte[].class);

            InputStream inputStream = new ByteArrayInputStream(bytes);

            String mimeType = URLConnection.guessContentTypeFromStream(inputStream);

            String fileName = FileHandling.prepareTempName("profile_pic", "jpg");

            MockMultipartFile mockMultipartFile = new MockMultipartFile(fileName, fileName, mimeType, bytes.length, bytes, inputStream);

            DocumentResponse documentResponse = documentService.uploadDocumentInternally(mockMultipartFile, documentStorage);

            if (!documentResponse.getStatus().equalsIgnoreCase(PortableConstants.FAILED) && documentResponse.getDocumentID() != null) {

                UserCredentials userCredentials = userCredentialsDao.getUserCredentials(userName);

                userCredentials.setProfilePicDMSId(documentResponse.getDocumentID());

                userCredentialsDao.saveOrUpdateUserCredentials(userCredentials);

            }

        } catch (Exception e) {
            logger.error(LoggerErrorConstants.EXCEPTION_OCCURRED, e);
        }

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
