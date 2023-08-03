package com.portabull.generic.dao;

import com.portabull.generic.models.OneTimePassword;
import com.portabull.payloads.OTPPayload;

public interface OTPDao {

    public OTPPayload generateOTP(int size, int expirationTimeInMins);

    public OTPPayload saveOneTimePassword(OTPPayload otpPayload);

    public OneTimePassword saveOneTimePassword(OneTimePassword oneTimePassword);

    public OneTimePassword getOneTimePassword(Long otpID);

    public boolean isValidOTP(OTPPayload otpPayload);

    public boolean isValidOTP(OneTimePassword oneTimePassword);

    public boolean deleteOneTImePassword(OneTimePassword oneTimePassword);

}
