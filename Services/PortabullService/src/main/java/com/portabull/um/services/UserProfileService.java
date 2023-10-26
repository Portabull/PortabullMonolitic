package com.portabull.um.services;

import com.portabull.cache.DBCacheUtils;
import com.portabull.cache.TokenCache;
import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.dms.service.DocumentService;
import com.portabull.payloads.TokenData;
import com.portabull.response.DocumentResponse;
import com.portabull.response.PortableResponse;
import com.portabull.um.UserCredentials;
import com.portabull.um.UserProfile;
import com.portabull.um.dao.UserCredentialsDao;
import com.portabull.utils.commonutils.CommonUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class UserProfileService {


    @Autowired
    DocumentService documentService;

    @Autowired
    UserCredentialsDao userCredentialsDao;

    public PortableResponse getProfilePicture() throws Exception {

        UserCredentials userCredential = userCredentialsDao.getUserCredential();

        if (userCredential.getProfilePicDMSId() == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("userName", CommonUtils.getLoggedInUserName());
            response.put("twoStepChecked", BooleanUtils.isTrue(userCredential.getTwoStepVerificationEnabled()) ? "checked" : "unchecked");
            response.put("singleSignInChecked", BooleanUtils.isTrue(userCredential.getSingleSignIn()) ? "checked" : "unchecked");
            response.put("mfaLoginType", userCredential.getMfaLoginType());
            return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, response);
        }

        DocumentResponse documentResponse = documentService.downloadDocument(userCredential.getProfilePicDMSId());

        if (PortableConstants.FAILED.equalsIgnoreCase(documentResponse.getStatus()))
            return new PortableResponse(documentResponse.getMessage(), documentResponse.getStatusCode(), documentResponse.getStatus(), documentResponse.getData());

        Map<String, Object> fileResponse = new HashMap<>();

        String contentType = "";
        if (documentResponse.getFileResponse().getFileName().endsWith(".pdf")) {
            contentType = MediaType.APPLICATION_PDF_VALUE;
        }

        fileResponse.put("file", "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(IOUtils.toByteArray(documentResponse.getFileResponse().getInputStream())));
        fileResponse.put("fileName", documentResponse.getFileResponse().getFileName());
        fileResponse.put("userName", CommonUtils.getLoggedInUserName());
        fileResponse.put("twoStepChecked", BooleanUtils.isTrue(userCredential.getTwoStepVerificationEnabled()) ? "checked" : "unchecked");
        fileResponse.put("singleSignInChecked", BooleanUtils.isTrue(userCredential.getSingleSignIn()) ? "checked" : "unchecked");
        fileResponse.put("mfaLoginType", userCredential.getMfaLoginType());
        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, fileResponse);
    }

    public PortableResponse uploadProfilePicture(MultipartFile file) throws IOException {

        DocumentResponse documentResponse;
        try {
            documentResponse = documentService.uploadDocument(file);
        } catch (Exception e) {
            throw new IOException(e);
        }

        if (documentResponse.getStatus().equalsIgnoreCase(PortableConstants.FAILED)) {
            return new PortableResponse("Internal Server Error", StatusCodes.C_500, PortableConstants.FAILED, null);
        }

        if (documentResponse.getDocumentID() != null) {

            UserCredentials userCredentials = userCredentialsDao.getUserCredential();

            userCredentials.setProfilePicDMSId(documentResponse.getDocumentID());

            userCredentialsDao.saveOrUpdateUserCredentials(userCredentials);

            return new PortableResponse("Profile Photo Uploaded Successfully", StatusCodes.C_200, PortableConstants.SUCCESS, null);

        }


        return new PortableResponse(documentResponse.getMessage(), documentResponse.getStatusCode(), documentResponse.getStatus(), documentResponse.getData());
    }

    public PortableResponse updateTwoStepVerification(Boolean twoStep, Boolean singleSignIn, Integer mfa, Integer sessionExpiredTime) {

        UserCredentials userCredentials = userCredentialsDao.getUserCredential();

        if (twoStep != null)
            userCredentials.setTwoStepVerificationEnabled(twoStep);

        if (singleSignIn != null)
            userCredentials.setSingleSignIn(singleSignIn);

        if (mfa != null)
            userCredentials.setMfaLoginType(mfa);

        if (sessionExpiredTime != null) {

            Long sessionTimeinMins = getSessionTimeinMins(sessionExpiredTime.longValue());

            userCredentials.setLoggedInSessionTime(sessionTimeinMins);

            TokenData tokenData = DBCacheUtils.get(CommonUtils.getAuthorizationToken());

            tokenData.setExpirationTime(sessionTimeinMins.intValue());

            DBCacheUtils.put(CommonUtils.getAuthorizationToken(), tokenData);

        }

        userCredentialsDao.saveOrUpdateUserCredentials(userCredentials);

        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, null);
    }

    public PortableResponse getUserNames(Map<String, Object> payload) {

        List<Object> userIds = (List<Object>) payload.get("userIds");

        CommonUtils.getLoggedInUserId();

        return userCredentialsDao.getUserNames(userIds);

    }

    public PortableResponse getUserIds(List<String> userNames) {

        return userCredentialsDao.getUserIds(userNames);

    }

    public PortableResponse getUserProfileInfo() throws IOException, IllegalBlockSizeException,
            NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException,
            ClassNotFoundException {

        Map<String, Object> fileResponse = new HashMap<>();

        UserCredentials userCredential = userCredentialsDao.getUserCredential();

        if (userCredential.getProfilePicDMSId() != null) {

            DocumentResponse documentResponse = documentService.downloadDocument(userCredential.getProfilePicDMSId());

            if (PortableConstants.FAILED.equalsIgnoreCase(documentResponse.getStatus()))
                return new PortableResponse(documentResponse.getMessage(), documentResponse.getStatusCode(), documentResponse.getStatus(), documentResponse.getData());

            String contentType = "";
            if (documentResponse.getFileResponse().getFileName().endsWith(".pdf")) {
                contentType = MediaType.APPLICATION_PDF_VALUE;
            }

            fileResponse.put("file", "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(IOUtils.toByteArray(documentResponse.getFileResponse().getInputStream())));
            fileResponse.put("fileName", documentResponse.getFileResponse().getFileName());
        }

        UserProfile userProfile = userCredentialsDao.getUserProfiles(userCredential.getUserID());

        fileResponse.put("userName", CommonUtils.getLoggedInUserName());
        fileResponse.put("twoStepChecked", BooleanUtils.isTrue(userCredential.getTwoStepVerificationEnabled()) ? "checked" : "unchecked");
        fileResponse.put("singleSignInChecked", BooleanUtils.isTrue(userCredential.getSingleSignIn()) ? "checked" : "unchecked");
        fileResponse.put("mfaLoginType", userCredential.getMfaLoginType());

        fileResponse.put("userName", CommonUtils.getLoggedInUserName());
        fileResponse.put("twoStepChecked", BooleanUtils.isTrue(userCredential.getTwoStepVerificationEnabled()) ? "checked" : "unchecked");
        fileResponse.put("singleSignInChecked", BooleanUtils.isTrue(userCredential.getSingleSignIn()) ? "checked" : "unchecked");
        fileResponse.put("mfaLoginType", userCredential.getMfaLoginType());
        fileResponse.put("userLoginName", CommonUtils.getLoggedInEmail());
        fileResponse.put("sessionTime", getSessionTimeinCode(userCredential.getLoggedInSessionTime()));
        fileResponse.put("googleUrl", "https://www.google.com/maps/search/");

        List<TokenCache> tokenCaches = DBCacheUtils.getTokenCache(CommonUtils.getLoggedInUserId());
        List<Map<String, Object>> deviceLocationDetails = new ArrayList<>();
        for (TokenCache tokenCache : tokenCaches) {
            Map<String, Object> deviceLocationDetail = new HashMap<>();
            if (tokenCache.getToken().equals(CommonUtils.getAuthorizationToken())) {
                deviceLocationDetail.put("currentUser", true);
            }

            deviceLocationDetail.put("locationDetails", tokenCache.getLocationDetails());
            if (!StringUtils.isEmpty(tokenCache.getDeviceDetails())) {
                String[] deviceDetails = tokenCache.getDeviceDetails().split(",");
                deviceLocationDetail.put("osName", deviceDetails[0].toLowerCase());
                deviceLocationDetail.put("osVersion", deviceDetails[1]);
                deviceLocationDetail.put("browserName", deviceDetails[2]);
                deviceLocationDetail.put("browserVersion", deviceDetails[3]);
            }
            deviceLocationDetail.put("deviceDetails", tokenCache.getDeviceDetails());
            deviceLocationDetail.put("id", tokenCache.getId());
            deviceLocationDetails.add(deviceLocationDetail);
        }

        fileResponse.put("locationDeviceDetails", deviceLocationDetails);

        if (userProfile != null) {
            fileResponse.put("mobileNumber", userProfile.getMobileNumber());
            fileResponse.put("address", userProfile.getAddress());
            fileResponse.put("address1", userProfile.getStreetAddress());
            fileResponse.put("city", userProfile.getCity());
            fileResponse.put("state", userProfile.getState());
            fileResponse.put("zip", userProfile.getPincode());
        }

        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, fileResponse);
    }

    private Integer getSessionTimeinCode(Long loggedInSessionTime) {

        if (loggedInSessionTime <= 59) {
            return loggedInSessionTime.intValue();
        } else if (loggedInSessionTime >= 60) {

            Long hours = loggedInSessionTime / 60;

            if (hours == 24) {
                return 83;
            }


            if (hours == 48) {
                return 84;
            }


            if (hours == 72) {
                return 85;
            }

            return 59 + hours.intValue();
        }

        return 0;
    }

    private Long getSessionTimeinMins(Long loggedInSessionTime) {

        if (loggedInSessionTime <= 59) {
            return loggedInSessionTime;
        } else if (loggedInSessionTime >= 60 && loggedInSessionTime <= 82) {
            Long hours = (loggedInSessionTime - 59);

            return hours * 60;

        } else if (loggedInSessionTime >= 83 && loggedInSessionTime <= 85) {

            Long days = (loggedInSessionTime - 82);

            return (days * 24) * 60;

        }
        return 0l;
    }

    public PortableResponse saveUserProfileInfo(Map<String, Object> payload) {

        UserProfile userProfile = userCredentialsDao.getUserProfiles(CommonUtils.getLoggedInUserId());

        userProfile.setCity(CommonUtils.getString(payload.get("city")));

        userProfile.setState(CommonUtils.getString(payload.get("state")));

        userProfile.setPincode(CommonUtils.getString(payload.get("zip")));

        userProfile.setAddress(CommonUtils.getString(payload.get("address")));

        userProfile.setStreetAddress(CommonUtils.getString(payload.get("address1")));

        userProfile.setMobileNumber(CommonUtils.getString(payload.get("mobileNumber")));

        UserCredentials userCredentials = userCredentialsDao.getUserCredentials(CommonUtils.getLoggedInEmail());

        userCredentials.setUserName(CommonUtils.getString(payload.get("userName")));

        userCredentialsDao.saveOrUpdateUserCredentials(userCredentials);

        userCredentialsDao.saveOrUpdateUserProfile(userProfile);

        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, null);

    }
}
