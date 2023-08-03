package com.portabull.um.services;

import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.dms.service.DocumentService;
import com.portabull.response.DocumentResponse;
import com.portabull.response.PortableResponse;
import com.portabull.um.UserCredentials;
import com.portabull.um.dao.UserCredentialsDao;
import com.portabull.utils.commonutils.CommonUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public PortableResponse updateTwoStepVerification(Boolean twoStep, Boolean singleSignIn, Integer mfa) {

        UserCredentials userCredentials = userCredentialsDao.getUserCredential();

        if (twoStep != null)
            userCredentials.setTwoStepVerificationEnabled(twoStep);

        if (singleSignIn != null)
            userCredentials.setSingleSignIn(singleSignIn);

        if (mfa != null)
            userCredentials.setMfaLoginType(mfa);

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
}
