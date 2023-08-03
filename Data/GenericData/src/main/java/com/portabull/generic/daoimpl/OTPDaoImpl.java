package com.portabull.generic.daoimpl;

import com.portabull.generic.dao.OTPDao;
import com.portabull.dbutils.HibernateUtils;
import com.portabull.generic.converters.OtpConverter;
import com.portabull.generic.models.OneTimePassword;
import com.portabull.payloads.OTPPayload;
import com.portabull.services.OTPService;
import com.portabull.utils.objectutils.ObjectHandling;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class OTPDaoImpl implements OTPDao {

    @Autowired
    HibernateUtils hibernateUtils;

    @Autowired
    OTPService otpService;

    @Override
    public OTPPayload generateOTP(int size, int expirationTimeInMins) {
        return saveOneTimePassword(otpService.generateOTP(size, expirationTimeInMins));
    }

    @Override
    public OTPPayload saveOneTimePassword(OTPPayload otpPayload) {
        return OtpConverter.convertOTPPayloadToEntity(
                hibernateUtils.saveOrUpdateEntity(
                        OtpConverter.convertOTPPayloadToEntity(otpPayload)
                )
        );
    }

    @Override
    public OneTimePassword saveOneTimePassword(OneTimePassword oneTimePassword) {
        return hibernateUtils.saveOrUpdateEntity(oneTimePassword);
    }

    @Override
    public OneTimePassword getOneTimePassword(Long otpID) {
        return hibernateUtils.findEntity(OneTimePassword.class, otpID);
    }

    @Override
    public boolean isValidOTP(OTPPayload otpPayload) {

        boolean validOTP;

        if (ObjectHandling.isNullObject(otpPayload) || StringUtils.isEmpty(otpPayload.getOtp()) || ObjectHandling.isNullObject(otpPayload.getOtpID()))
            return false;

        OneTimePassword oneTimePassword = hibernateUtils.findEntity(OneTimePassword.class, otpPayload.getOtpID());

        if (ObjectHandling.isNullObject(oneTimePassword)) {
            validOTP = false;
        } else if (otpPayload.getOtp().equals(oneTimePassword.getOtp())) {
            deleteOneTImePassword(oneTimePassword);
            validOTP = true;
        } else {
            validOTP = false;
        }

        return validOTP;
    }

    @Override
    public boolean isValidOTP(OneTimePassword oneTimePassword) {

        boolean validOTP;

        if (ObjectHandling.isNullObject(oneTimePassword) || StringUtils.isEmpty(oneTimePassword.getOtp()) || ObjectHandling.isNullObject(oneTimePassword.getOtpID()))
            return false;

        OneTimePassword oneTimePasswordENtity = hibernateUtils.findEntity(OneTimePassword.class, oneTimePassword.getOtpID());

        if (ObjectHandling.isNullObject(oneTimePasswordENtity)) {
            validOTP = false;
        } else if (oneTimePassword.getOtp().equals(oneTimePasswordENtity.getOtp())) {
            deleteOneTImePassword(oneTimePassword);
            validOTP = true;
        } else {
            validOTP = false;
        }

        return validOTP;
    }

    @Override
    public boolean deleteOneTImePassword(OneTimePassword oneTimePassword) {
        hibernateUtils.deleteEntity(OneTimePassword.class, oneTimePassword.getOtpID());
        return true;
    }
}
