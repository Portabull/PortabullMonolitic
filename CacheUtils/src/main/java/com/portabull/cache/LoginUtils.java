package com.portabull.cache;

import com.portabull.constants.MessageConstants;
import com.portabull.constants.PortableConstants;
import com.portabull.payloads.TokenData;
import com.portabull.response.PortableResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Calendar;

@Service
public class LoginUtils {

    public PortableResponse isValidToken(String token, String originalRequestPath) {
        TokenData tokenData = null;
        if (CacheUtils.get(token) instanceof TokenData)
            tokenData = (TokenData) CacheUtils.get(token);

        if (tokenData != null) {

            Calendar calendar = Calendar.getInstance();
            if (tokenData.getEndTime() >= calendar.getTimeInMillis()) {

                if (!"/APIGateway/UM/validateOtp".equalsIgnoreCase(originalRequestPath)) {
                    if (tokenData.isTwoStepVerificationEnabled() && !tokenData.isValidatedTwoStepAuth()) {
                        return new PortableResponse("Two step auth is required", 401L, PortableConstants.SUCCESS, null);
                    }
                } else {
                    if (isAlreadyLoggedIn(((TokenData) CacheUtils.get(token)).getUserID())) {
                        return new PortableResponse(MessageConstants.SIGLE_SIGN_IN_ERR, 401L, PortableConstants.SUCCESS, null);
                    }
                }
                addTokenToCache(token, tokenData.getExpirationTime(), tokenData);
                return new PortableResponse("Successfully LoggedIn", 200L, PortableConstants.SUCCESS, null);
            } else {
                CacheUtils.remove(token);
                return new PortableResponse("Session Expired", 401L, PortableConstants.SUCCESS, null);
            }
        }
        return new PortableResponse("Unauthorized Access", 401L, PortableConstants.SUCCESS, null);
    }

    public void addTokenToCache(String jwtToken, Integer validTimeUntilInMins, TokenData tokenData) {
        Calendar calendar = Calendar.getInstance();
        tokenData.setStartTime(calendar.getTimeInMillis());
        calendar.add(Calendar.MINUTE, validTimeUntilInMins);
        tokenData.setEndTime(calendar.getTimeInMillis());
        tokenData.setExpirationTime(validTimeUntilInMins);
        CacheUtils.store(jwtToken, tokenData);
    }

    public static String getUserNameByToken(String token) {
        TokenData tokenData = null;
        if (CacheUtils.get(token) instanceof TokenData)
            tokenData = (TokenData) CacheUtils.get(token);
        if (tokenData != null) {
            return tokenData.getUserName();
        }
        return null;
    }

    public static void logout(String token) {
        CacheUtils.remove(token);
    }

    public static void validatedTwoStepAuth() {
        String token = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest().getHeader("Authorization").replace("Bearer ", "");
        TokenData tokenData = (TokenData) CacheUtils.get(token);
        tokenData.setValidatedTwoStepAuth(true);
        CacheUtils.store(token, tokenData);
    }

    public boolean isAlreadyLoggedIn(Long userID) {
        return CacheUtils.isAlreadyLoggedIn(userID);
    }

}
