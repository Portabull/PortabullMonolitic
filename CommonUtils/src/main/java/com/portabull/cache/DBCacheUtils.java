package com.portabull.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portabull.dbutils.HibernateUtils;
import com.portabull.execption.DataParseException;
import com.portabull.payloads.TokenData;
import com.portabull.utils.commonutils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Calendar;
import java.util.List;

@Component
public class DBCacheUtils {

    static HibernateUtils hibernateUtils;

    static ObjectMapper mapper = new ObjectMapper();

    public static boolean isAlreadyLoggedIn(String token) {

        TokenCache tokenData = hibernateUtils.findEntityByCriteria(TokenCache.class, "token", token);

        List<TokenCache> tokenCaches = hibernateUtils.findEntitiesByCriteria(TokenCache.class, "userID", tokenData.getUserID());

        if (!CollectionUtils.isEmpty(tokenCaches)) {
            for (TokenCache tokenCach : tokenCaches) {
                if (isTokenExpired(tokenCach)) {
                    remove(tokenCach.getToken());
                    return false;
                }

                if (tokenCach.isTwoStepVerificationEnabled()) {
                    //validate two step verification
                    return tokenCach.isValidatedTwoStepAuth();
                }
                return true;
            }
        }

        return false;
    }

    public static boolean isAlreadyLoggedIn(Long userId) {

        List<TokenCache> tokenCaches = hibernateUtils.findEntitiesByCriteria(TokenCache.class, "userID", userId);


        if (!CollectionUtils.isEmpty(tokenCaches)) {
            for (TokenCache tokenCach : tokenCaches) {
                if (isTokenExpired(tokenCach)) {
                    remove(tokenCach.getToken());
                    return false;
                }

                if (tokenCach.isTwoStepVerificationEnabled()) {
                    //validate two step verification
                    return tokenCach.isValidatedTwoStepAuth();
                }
                return true;
            }
        }

        return false;
    }

    public static boolean isTokenExpired(TokenCache tokenData) {
        Calendar calendar = Calendar.getInstance();
        if (tokenData.getEndTime() >= calendar.getTimeInMillis()) {
            return false;
        }
        return true;
    }

    @Autowired
    public synchronized void setHibernateUtils(HibernateUtils hibernateUtils) {
        DBCacheUtils.hibernateUtils = hibernateUtils;
    }

    public static void put(String key, TokenData tokenData) {
        TokenCache token = hibernateUtils.findEntityByCriteria(TokenCache.class, "token", key);

        if (token == null) {
            token = new TokenCache();
        }
        token.setUserID(tokenData.getUserID());
        token.setValidatedTwoStepAuth(tokenData.isValidatedTwoStepAuth());
        token.setSingleSignOn(tokenData.isSingleSignOn());
        token.setTwoStepVerificationEnabled(tokenData.isTwoStepVerificationEnabled());
        token.setEmail(tokenData.getEmail());
        token.setEndTime(tokenData.getEndTime());
        token.setExpirationTime(tokenData.getExpirationTime());
        token.setStartTime(tokenData.getStartTime());
        token.setUserName(tokenData.getUserName());
        token.setDeviceDetails(tokenData.getDeviceDetails());
        token.setLocationDetails(tokenData.getLocationDetails());
        token.setToken(key);

        hibernateUtils.saveOrUpdateEntity(token);
    }

    public static TokenData get(String key) {
        TokenCache token = hibernateUtils.findEntityByCriteria(TokenCache.class, "token", key);
        if (token != null) {
            TokenData data = new TokenData();
            data.setUserID(token.getUserID());
            data.setValidatedTwoStepAuth(token.isValidatedTwoStepAuth());
            data.setSingleSignOn(token.isSingleSignOn());
            data.setTwoStepVerificationEnabled(token.isTwoStepVerificationEnabled());
            data.setEmail(token.getEmail());
            data.setEndTime(token.getEndTime());
            data.setExpirationTime(token.getExpirationTime());
            data.setStartTime(token.getStartTime());
            data.setUserName(token.getUserName());
            data.setLocationDetails(token.getLocationDetails());
            data.setDeviceDetails(token.getDeviceDetails());
            return data;
        }

        return null;
    }

    public static void remove(String key) {
        TokenCache token = hibernateUtils.findEntityByCriteria(TokenCache.class, "token", key);
        hibernateUtils.deleteEntity(TokenCache.class, token.getId());
    }

    public static synchronized void storeCache(String key, Object value) {
        try {

            String json = mapper.writeValueAsString(value);

            String authorizationToken = CommonUtils.getAuthorizationToken();

            ApplicationUserCache applicationUserCache = hibernateUtils.findEntityByCriteria(ApplicationUserCache.class, "key", key,
                    "token", authorizationToken);

            if (applicationUserCache == null) {
                applicationUserCache = new ApplicationUserCache();
            }

            applicationUserCache.setData(json);

            applicationUserCache.setToken(authorizationToken);

            applicationUserCache.setKey(key);

            hibernateUtils.saveOrUpdateEntity(applicationUserCache);

        } catch (JsonProcessingException e) {
            throw new DataParseException(e, "Unable to parse value");
        }
    }

    public static <T> T getCache(String key, Class responseType) {
        try {
            String authorizationToken = CommonUtils.getAuthorizationToken();

            ApplicationUserCache applicationUserCache = hibernateUtils.findEntityByCriteria(ApplicationUserCache.class, "key", key,
                    "token", authorizationToken);

            if (applicationUserCache != null) {
                return (T) mapper.readValue(applicationUserCache.getData(), responseType);
            }

        } catch (JsonProcessingException e) {
            throw new DataParseException(e, "Unable to parse value");
        }
        return null;
    }

    public static synchronized void storeApplicationLevelCache(String key, Object value) {
        try {

            String json = mapper.writeValueAsString(value);

            ApplicationCache applicationUserCache = hibernateUtils.findEntityByCriteria(ApplicationCache.class, "key", key);

            if (applicationUserCache == null) {
                applicationUserCache = new ApplicationCache();
            }

            applicationUserCache.setData(json);

            applicationUserCache.setKey(key);

            hibernateUtils.saveOrUpdateEntity(applicationUserCache);

        } catch (JsonProcessingException e) {
            throw new DataParseException(e, "Unable to parse value");
        }
    }

    public static <T> T getApplicationLevelCache(String key, Class responseType) {
        try {

            ApplicationCache applicationCache = hibernateUtils.findEntityByCriteria(ApplicationCache.class, "key", key);

            if (applicationCache != null) {
                return (T) mapper.readValue(applicationCache.getData(), responseType);
            }

        } catch (JsonProcessingException e) {
            throw new DataParseException(e, "Unable to parse value");
        }
        return null;
    }

    public static List<TokenCache> getTokenCache(Long userID) {
        return hibernateUtils.findEntitiesByCriteria(TokenCache.class, "userID", userID);
    }

    public static void removeTokenCache(Long sessionId) {
        hibernateUtils.deleteEntity(TokenCache.class, sessionId);
    }


}
