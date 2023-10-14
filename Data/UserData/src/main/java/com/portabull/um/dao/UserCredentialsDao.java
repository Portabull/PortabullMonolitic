package com.portabull.um.dao;

import com.portabull.response.PortableResponse;
import com.portabull.um.UserCredentials;
import com.portabull.um.UserProfile;

import java.util.List;

public interface UserCredentialsDao {

    public UserCredentials getUserCredential();

    public UserCredentials getUserCredentials(String userName);

    public UserCredentials getUserCredential(String userName, String password);

    public boolean updateWrongPasswordCount(UserCredentials userCredentials, boolean isValidUser) throws Exception;

    public UserProfile getUserProfiles(Long userID);

    public boolean isAlreadyUserExists(String userName, String password, String email);

    public PortableResponse registration(String userName, String password, String email, String otp, String token, boolean isLoggedWithOAuth);

    public PortableResponse validateRegistrationOtp(String authorizationToken, String otp);

    PortableResponse notifyAdministrator(String emailAddress, String reason);

    public UserCredentials saveOrUpdateUserCredentials(UserCredentials userCredentials);

    PortableResponse getUserNames(List<Object> userIds);

    PortableResponse getUserIds(List<String> userNames);

    UserProfile saveOrUpdateUserProfile(UserProfile userProfile);
}
