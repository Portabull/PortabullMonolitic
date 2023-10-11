package com.portabull.utils.commonutils;

import com.portabull.cache.CacheData;
import com.portabull.cache.DBCacheUtils;
import com.portabull.cache.TokenCache;
import com.portabull.execption.TokenNotFoundException;
import com.portabull.payloads.TokenData;
import com.portabull.utils.RequestHelper;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
public class CommonUtils {

    static Map<String, Object> cache;

//    @Autowired
//    public synchronized void setIMap(IMap<String, Object> imap) {
//        cache = imap;
//    }

    @Autowired
    public synchronized void setIMap(CacheData cacheData) {
        cache = cacheData.getData();
    }

    public static <T> ResponseEntity<T> returnResponse(T response, HttpStatus status) {
        return new ResponseEntity<>(response, status);
    }

    public static Long getLoggedInUserId() {
        String token = CommonUtils.getAuthorizationToken();

        if (StringUtils.isEmpty(token))
            throw new TokenNotFoundException("Token Not Found");

        TokenData tokenData = DBCacheUtils.get(token);
        if (tokenData == null || DBCacheUtils.isTokenExpired(getTokenCache(tokenData))) {
            throw new TokenNotFoundException("Token Expired Or No User Found for this token");
        }

        return tokenData.getUserID();
    }

    public static void validateToken() {
        String token = CommonUtils.getAuthorizationToken();

        if (StringUtils.isEmpty(token))
            throw new TokenNotFoundException("Token Not Found");

        TokenData tokenData = DBCacheUtils.get(token);

        if (tokenData == null || DBCacheUtils.isTokenExpired(getTokenCache(tokenData)) || (tokenData.isTwoStepVerificationEnabled() && !tokenData.isValidatedTwoStepAuth())) {
            throw new TokenNotFoundException("Token Expired Or No User Found for this token");
        }

    }

    private static TokenCache getTokenCache(TokenData tokenData) {
        TokenCache tokenCache = new TokenCache();
        tokenCache.setEmail(tokenData.getEmail());
        tokenCache.setEndTime(tokenData.getEndTime());
        tokenCache.setExpirationTime(tokenData.getExpirationTime());
        tokenCache.setStartTime(tokenData.getStartTime());
        tokenCache.setSingleSignOn(tokenData.isSingleSignOn());
        tokenCache.setUserID(tokenData.getUserID());
        tokenCache.setUserName(tokenData.getUserName());
        tokenCache.setTwoStepVerificationEnabled(tokenData.isTwoStepVerificationEnabled());
        tokenCache.setValidatedTwoStepAuth(tokenData.isValidatedTwoStepAuth());
        return tokenCache;
    }

    public static String getLoggedInUserName() {
        String token = CommonUtils.getAuthorizationToken();

        if (StringUtils.isEmpty(token))
            throw new TokenNotFoundException("Token Not Found");

        TokenData tokenData = DBCacheUtils.get(token);
        if (tokenData == null) {
            throw new TokenNotFoundException("Token Expired Or No User Found for this token");
        }

        return tokenData.getUserName();
    }

    public static String getAuthorizationToken() {
        String authorization = RequestHelper.getCurrentRequest().getHeader("Authorization");
        if (!StringUtils.isEmpty(authorization)) {
            return authorization.replace("Bearer", "").trim();
        } else if (!StringUtils.isEmpty(RequestHelper.getCurrentRequest().getAttribute("Authorization"))) {
            return RequestHelper.getCurrentRequest().getAttribute("Authorization").toString().replace("Bearer", "").trim();
        }
        return null;
    }

    public static Long getLoggedInUserIdDummy() {
//        TokenData tokenData = null;
//        String token = CommonUtils.getAuthorizationToken();
//        if(!StringUtils.isEmpty(token)){
//            if (cache.get(token) instanceof TokenData)
//                tokenData = (TokenData) cache.get(token);
//            if (tokenData != null) {
//                return tokenData.getUserID();
//            }
//        }
        return 1L;
    }

    public static String getRandomString() {

        StringBuilder randomBuilder = new StringBuilder(RandomStringUtils.random(2, true, false));

        randomBuilder.append(RandomStringUtils.random(3, false, true));

        randomBuilder.append(RandomStringUtils.random(1, true, false));

        randomBuilder.append(RandomStringUtils.random(4, false, true));

        randomBuilder.append(RandomStringUtils.random(2, true, false));

        randomBuilder.append(RandomStringUtils.random(4, false, true));

        randomBuilder.append(RandomStringUtils.random(2, true, false));

        randomBuilder.append(RandomStringUtils.random(2, true, false));

        randomBuilder.append(RandomStringUtils.random(3, false, true));

        randomBuilder.append(RandomStringUtils.random(3, false, true));

        randomBuilder.append(RandomStringUtils.random(1, true, false));

        randomBuilder.append(RandomStringUtils.random(4, false, true));

        randomBuilder.append(RandomStringUtils.random(2, true, false));

        randomBuilder.append(RandomStringUtils.random(4, false, true));

        randomBuilder.append(RandomStringUtils.random(2, true, false));

        randomBuilder.append(RandomStringUtils.random(2, true, false));

        randomBuilder.append(RandomStringUtils.random(3, false, true));

        randomBuilder.append(RandomStringUtils.random(3, false, true));

        randomBuilder.append(RandomStringUtils.random(1, true, false));

        randomBuilder.append(RandomStringUtils.random(4, false, true));

        randomBuilder.append(RandomStringUtils.random(2, true, false));

        randomBuilder.append(RandomStringUtils.random(4, false, true));

        randomBuilder.append(RandomStringUtils.random(2, true, false));

        randomBuilder.append(RandomStringUtils.random(2, true, false));

        randomBuilder.append(RandomStringUtils.random(3, false, true));

        randomBuilder.append(RandomStringUtils.random(3, false, true));

        randomBuilder.append(RandomStringUtils.random(1, true, false));

        randomBuilder.append(RandomStringUtils.random(4, false, true));

        randomBuilder.append(RandomStringUtils.random(2, true, false));

        randomBuilder.append(RandomStringUtils.random(4, false, true));

        randomBuilder.append(RandomStringUtils.random(2, true, false));

        randomBuilder.append(RandomStringUtils.random(2, true, false));

        randomBuilder.append(RandomStringUtils.random(3, false, true));

        return randomBuilder.toString();
    }

    public static void setAuthorizationToken(String token) {
        RequestHelper.getCurrentRequest().setAttribute("Authorization", "Bearer " + token);
    }


}
