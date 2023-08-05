package com.portabull.um.services;

import com.portabull.cache.CacheUtils;
import com.portabull.cache.DBCacheUtils;
import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.generic.dao.CommonDao;
import com.portabull.payloads.TokenData;
import com.portabull.response.PortableResponse;
import com.portabull.um.NotificationUserJWTToken;
import com.portabull.utils.encoder.EncoderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class NotificationMFAService {

    @Autowired
    CommonDao commonDao;


    public PortableResponse verifyRandomToken(String token) {

        PortableResponse response = new PortableResponse();

        NotificationUserJWTToken randomTokenDetails = commonDao.findEntity(NotificationUserJWTToken.class,
                "randomTokenId", token);

        Map<String, Object> resp = new LinkedHashMap<>();

        resp.put("a", randomTokenDetails.getApproval());

        if (randomTokenDetails.getApproval() == 1) {

            resp.put("token", randomTokenDetails.getJwtToken());

        }

        response.setData(resp);

        return response.setStatusCode(200L);

    }

    public PortableResponse approveDenyRequest(String token, Integer status) {

        token = notificationDecoder(token);


        NotificationUserJWTToken randomTokenDetails = commonDao.findEntity(NotificationUserJWTToken.class,
                "randomTokenId", token);

        if (randomTokenDetails == null) {

            return new PortableResponse("Link Expired", StatusCodes.C_401, PortableConstants.FAILED, null);

        }

        if (status != 1 && status != 2) {

            return new PortableResponse("Invalid Status", StatusCodes.C_400, PortableConstants.FAILED, null);

        }

        if (status == 1) {
            TokenData tokenData = DBCacheUtils.get(randomTokenDetails.getJwtToken());
            tokenData.setValidatedTwoStepAuth(true);
            DBCacheUtils.put(randomTokenDetails.getJwtToken(), tokenData);
        }

        if (status == 2) {
            TokenData tokenData = DBCacheUtils.get(randomTokenDetails.getJwtToken());
            tokenData.setValidatedTwoStepAuth(false);
            DBCacheUtils.put(randomTokenDetails.getJwtToken(), tokenData);
        }

        randomTokenDetails.setApproval(status);

        commonDao.saveOrUpdateEntity(randomTokenDetails);

        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, null);

    }

    public static String notificationEncoder(String token){

        StringBuilder levelOneEncryption = new StringBuilder(EncoderUtils.encode(token)).reverse();

        return EncoderUtils.encode(levelOneEncryption.toString());


    }

    public static String notificationDecoder(String token){

        String levelOneDecryption = EncoderUtils.decode(token);

        return EncoderUtils.decode(new StringBuilder(levelOneDecryption).reverse().toString());

    }


}
