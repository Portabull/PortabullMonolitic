package com.portabull.utils.configurations;

import com.portabull.utils.smsutils.SmsHelperUtils;
import com.portabull.utils.smsutils.TwilioSmsService;
import com.twilio.Twilio;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;


@Configuration
public class SMSSenderConfiguration extends BeanConfiguration {

    static Logger logger = LoggerFactory.getLogger(SMSSenderConfiguration.class);

    @Value("${twilio.account_sid:#{null}}")
    private String accountSid;

    @Value("${twilio.auth_token:#{null}}")
    private String authToken;

    @Value("${twilio.trial_number:#{null}}")
    private String trialNumber;

    @Value("${twilio.whatsapp.trial_number:#{null}}")
    private String whatsAppTrialNumber;

    @Value("${sms.helper.classType:#{null}}")
    private String smsClassType;

    public String getAccountSid() {
        return accountSid;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getTrialNumber() {
        return trialNumber;
    }

    public String getSmsClassType() {
        return smsClassType;
    }

    @Bean
    public SmsHelperUtils invokeSmsService() throws ClassNotFoundException {

        if (!StringUtils.isEmpty(accountSid) && !StringUtils.isEmpty(authToken)) {
            Twilio.init(
                    this.accountSid,
                    this.authToken
            );
            TwilioSmsService.setPhoneNumber(new PhoneNumber(this.trialNumber));
            TwilioSmsService.setWhatsAppNumber(new PhoneNumber("whatsapp:" + this.whatsAppTrialNumber));
            logger.info("Twilio initialized ... with account sid {} ", accountSid);
        } else {
            logger.info("Uninitialized the Twilio cause account sid / authToken is null...");
        }

        if (StringUtils.isEmpty(smsClassType)) {
            return applicationContext.getBean(TwilioSmsService.class);
        }

        return (SmsHelperUtils) applicationContext.getBean(Class.forName(smsClassType));
    }


}
