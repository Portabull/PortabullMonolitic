package com.portabull.cache;

import com.portabull.execption.AlreadyInitializedExecption;
import com.portabull.payloads.TokenData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * to storing the cache mechanism
 */

@Service
public class CacheUtils {

    private static Map<String, Object> cache;

    static Logger logger = LoggerFactory.getLogger(CacheUtils.class);

//    @Autowired
//    public synchronized void setIMap(IMap<String, Object> imap) throws AlreadyInitializedExecption {
//        if (cache != null) {
//            throw new AlreadyInitializedExecption("Already cache was initialized");
//        }
//        cache = imap;
//    }

//    @Autowired
//    public synchronized void setIMap(CacheData cacheData) throws AlreadyInitializedExecption {
//        if (cache != null) {
//            throw new AlreadyInitializedExecption("Already cache was initialized");
//        }
//        cache = cacheData.getData();
//    }

//    public static void store(String key, Object value) {
//        cache.put(key, value);
//    }

//    public static Object get(String key) {
//        return cache.get(key);
//    }

//    public static void remove(String key) {
//        cache.remove(key);
//    }

//    public static boolean isAlreadyLoggedIn(Long userID) {
//        TokenData data;
//        for (Map.Entry<String, Object> entry : cache.entrySet()) {
//            if (entry.getValue() instanceof TokenData && ((TokenData) entry.getValue()).getUserID().equals(userID)) {
//                data = (TokenData) entry.getValue();
//                if (isTokenExpired(data)) {
//                    remove(entry.getKey());
//                    return false;
//                }
//
//                if (data.isTwoStepVerificationEnabled()) {
//                    //validate two step verification
//                    return data.isValidatedTwoStepAuth();
//                }
//                return true;
//            }
//        }
//        return false;
//    }



    @PreDestroy
    public void destory() {
        cache = null;
        logger.info("Destroyed....");
    }
}