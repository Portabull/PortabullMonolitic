package com.portabull.um.utils;

import com.portabull.cache.DBCacheUtils;
import com.portabull.cache.LoginUtils;
import com.portabull.constants.MessageConstants;
import com.portabull.constants.PortableConstants;
import com.portabull.execption.ServerException;
import com.portabull.execption.SingleSignInEnabledException;
import com.portabull.payloads.AuthenticationRequest;
import com.portabull.payloads.TokenData;
import com.portabull.um.UserCredentials;
import com.portabull.um.dao.UserCredentialsDao;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {

    @Autowired
    UserCredentialsDao userCredentialsDao;

    @Autowired
    LoginUtils loginUtils;

    public static final String SECRET = "secret";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserCredentials userCredentials, boolean isAdminLogin, boolean skipMFA) {

        if (!isAdminLogin && BooleanUtils.isTrue(DBCacheUtils.getApplicationLevelCache(PortableConstants.BLOCK_LOG_INS, Boolean.class))) {
            throw new ServerException("Server is under maintenance");
        }

        if (BooleanUtils.isTrue(userCredentials.getSingleSignIn()) && DBCacheUtils.isAlreadyLoggedIn(userCredentials.getUserID())) {
            throw new SingleSignInEnabledException(MessageConstants.SIGLE_SIGN_IN_ERR);
        }

        Map<String, Object> claims = new HashMap<>();

        String jwtToken = createToken(claims, userCredentials.getLoginUserName());

        loginUtils.addTokenToCache(jwtToken, userCredentials.getLoggedInSessionTime().intValue(), getTokenData(userCredentials, skipMFA));

        return jwtToken;
    }

    private TokenData getTokenData(UserCredentials userCredentials, boolean skipMFA) {
        TokenData tokenData = new TokenData();
        tokenData.setUserName(userCredentials.getUserName());
        tokenData.setEmail(userCredentials.getLoginUserName());
        tokenData.setUserID(userCredentials.getUserID());
        if (!skipMFA)
            tokenData.setTwoStepVerificationEnabled(userCredentials.getTwoStepVerificationEnabled());
        tokenData.setSingleSignOn(BooleanUtils.isTrue(userCredentials.getSingleSignIn()));
        return tokenData;
    }


    private String createToken(Map<String, Object> claims, String subject) {

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public void decryptCred(AuthenticationRequest authenticationRequest) {

        Base64.Decoder decoder = Base64.getDecoder();

        authenticationRequest.setUsername(
                new String(decoder.decode(new StringBuilder(new String(decoder.decode(authenticationRequest.getUsername()))).reverse().toString()))
        );

        authenticationRequest.setPassword(
                new String(decoder.decode(new StringBuilder(new String(decoder.decode(authenticationRequest.getPassword()))).reverse().toString()))
        );

    }

}
