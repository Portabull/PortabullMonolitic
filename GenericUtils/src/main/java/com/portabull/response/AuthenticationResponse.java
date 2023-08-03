package com.portabull.response;

import java.io.Serializable;

public class AuthenticationResponse implements Serializable {

    private final String jwt;

    private final boolean twostepVerificationEnabled;

    private final boolean notificationType;

    private final String randomToken;

    public AuthenticationResponse(String jwt, boolean twostepVerificationEnabled, boolean notificationType, String randomToken) {
        this.jwt = jwt;
        this.twostepVerificationEnabled = twostepVerificationEnabled;
        this.notificationType = notificationType;
        this.randomToken = randomToken;
    }

    public String getJwt() {
        return jwt;
    }

    public boolean isTwostepVerificationEnabled() {
        return twostepVerificationEnabled;
    }

    public boolean isNotificationType() {
        return notificationType;
    }

    public String getRandomToken() {
        return randomToken;
    }
}
