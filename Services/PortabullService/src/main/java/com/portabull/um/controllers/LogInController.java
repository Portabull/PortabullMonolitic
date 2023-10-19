package com.portabull.um.controllers;

import com.netflix.zuul.context.RequestContext;
import com.portabull.cache.DBCacheUtils;
import com.portabull.cache.LoginUtils;
import com.portabull.constants.LoggerErrorConstants;
import com.portabull.constants.MessageConstants;
import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.execption.BadRequestException;
import com.portabull.execption.ServerException;
import com.portabull.execption.SingleSignInEnabledException;
import com.portabull.payloads.AuthenticationRequest;
import com.portabull.payloads.EmailPayload;
import com.portabull.payloads.TokenData;
import com.portabull.response.AuthenticationResponse;
import com.portabull.response.EmailResponse;
import com.portabull.response.PortableResponse;
import com.portabull.um.UserCredentials;
import com.portabull.um.UserProfile;
import com.portabull.um.services.MyUserDetailsService;
import com.portabull.um.services.OTPValidationService;
import com.portabull.um.utils.JwtUtil;
import com.portabull.utils.RequestHelper;
import com.portabull.utils.commonutils.CommonUtils;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("UM")
public class LogInController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @PostMapping("/hello")
    public String firstPage() {
        return "Hello World";
    }

    @Autowired
    LoginUtils loginUtils;

    @Autowired
    OTPValidationService otpValidationService;

    Logger logger = LoggerFactory.getLogger(LogInController.class);

    @PostMapping(value = "/login")
    public ResponseEntity<PortableResponse> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest,
                                                                      boolean disableAuth) {
        String randomToken = "";
        if (!disableAuth)
            jwtTokenUtil.decryptCred(authenticationRequest);

        UserCredentials userCredentials = new UserCredentials(authenticationRequest.getUsername());

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(), authenticationRequest.getPassword()));

            userCredentials = userDetailsService.getUserCredentials(authenticationRequest.getUsername());

            userDetailsService.updateWrongPasswordCount(userCredentials, true);

            String jwt = jwtTokenUtil.generateToken(userCredentials, false, false);

            if (BooleanUtils.isTrue(userCredentials.getTwoStepVerificationEnabled()) && userCredentials.getMfaLoginType() != null
                    && userCredentials.getMfaLoginType() == 1) {
                randomToken = userDetailsService.saveJwtDetailsToNotify(jwt, userCredentials);
                //if two-step verification is enabled with email link auth jwt we are not giving in login
                jwt = null;
            }

            return new ResponseEntity<>(new PortableResponse(MessageConstants.LOGIN_SUCCESS,
                    200L, PortableConstants.SUCCESS, new AuthenticationResponse(jwt,
                    BooleanUtils.isTrue(userCredentials.getTwoStepVerificationEnabled()),
                    userCredentials.getMfaLoginType() != null && userCredentials.getMfaLoginType() == 1, randomToken)), HttpStatus.OK);
        } catch (BadCredentialsException e) {
            //we can get isInvalidPassword for this method
            userDetailsService.updateWrongPasswordCount(userCredentials, false);
            return CommonUtils.returnResponse(new PortableResponse(MessageConstants.INVALID_CREDENTIALS,
                    401L, PortableConstants.FAILED, null), HttpStatus.UNAUTHORIZED);
        } catch (LockedException lockedException) {
            return CommonUtils.returnResponse(new PortableResponse(MessageConstants.ACCOUNT_LOCKED,
                    401L, PortableConstants.SUCCESS, null), HttpStatus.UNAUTHORIZED);
        } catch (DisabledException ee) {
            return CommonUtils.returnResponse(new PortableResponse(MessageConstants.ADMIN_ACCOUNT_LOCKED,
                    401L, PortableConstants.SUCCESS, null), HttpStatus.UNAUTHORIZED);
        } catch (SingleSignInEnabledException e) {
            return new ResponseEntity<>(new PortableResponse(e.getMessage(),
                    401L, PortableConstants.FAILED, null), HttpStatus.UNAUTHORIZED);
        } catch (ServerException e) {
            return new ResponseEntity<>(new PortableResponse(e.getMessage(),
                    503L, PortableConstants.FAILED, null), HttpStatus.SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            logger.error(LoggerErrorConstants.EXCEPTION_OCCURRED, e);
            return CommonUtils.returnResponse(new PortableResponse(MessageConstants.SERVICE_FAILED,
                    500L, PortableConstants.FAILED, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<PortableResponse> validate(HttpServletRequest rawData) {
        String token = RequestHelper.getAuthorizationToken(rawData);
        String requestPath = (String) RequestContext.getCurrentContext().get(FilterConstants.REQUEST_URI_KEY);
        return new ResponseEntity<>(loginUtils.isValidToken(token, requestPath), HttpStatus.OK);
    }

    @PostMapping(value = "/plogout")
    public ResponseEntity<PortableResponse> logout() {
        String token = RequestHelper.getAuthorizationToken(RequestHelper.getCurrentRequest());
        LoginUtils.logout(token);
        return new ResponseEntity<>(new PortableResponse()
                .setMessage("Logout Successfully")
                .setStatus(PortableConstants.SUCCESS).setStatusCode(200L), HttpStatus.OK);
    }

    @PostMapping(value = "/logout-session")
    public ResponseEntity<?> logoutSession(@RequestParam String sessionId) {

        DBCacheUtils.removeTokenCache(Long.valueOf(sessionId));

        return new ResponseEntity<>(new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, null), HttpStatus.OK);

    }

    @PostMapping(value = "/cache-logged-dev-dtls")
    public ResponseEntity<?> cacheLoggedDevDtls() {

        TokenData tokenData = DBCacheUtils.get(CommonUtils.getAuthorizationToken());

        tokenData.setDeviceDetails(RequestHelper.getCurrentRequest().getHeader("devdtls"));

        String location = RequestHelper.getCurrentRequest().getHeader("location");
        String latLong = "";
        if (!StringUtils.isEmpty(location) && !"null".equalsIgnoreCase(location)) {
            latLong = latLong + location.split(";")[0];
            latLong = latLong + "," + location.split(";")[1];
        }

        tokenData.setLocationDetails(latLong);

        DBCacheUtils.put(CommonUtils.getAuthorizationToken(),tokenData);

        return new ResponseEntity<>(new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, null), HttpStatus.OK);
    }


    @PostMapping("/getUserProfiles")
    public ResponseEntity<UserProfile> getUserProfiles(@RequestParam Long userID) {
        return new ResponseEntity<>(userDetailsService.getUserProfiles(userID), HttpStatus.OK);
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody Map<String, Object> payload) {

        String userName = payload.get("username").toString();
        String password = payload.get("password").toString();
        String email = payload.get("email").toString();
        String otp = payload.get("otp").toString();
        String token = payload.get("token").toString();

        if (userDetailsService.isAlreadyUserExists(userName, password, email)) {
            return new ResponseEntity<>(new PortableResponse("Already User Exists"
                    , 200L, PortableConstants.FAILED, null), HttpStatus.OK);
        }

        PortableResponse portableResponse = userDetailsService.registration(userName, password, email, otp, token);

        if (portableResponse.getStatusCode() == 200) {
            return createAuthenticationToken(new AuthenticationRequest(email, password), true);
        }

        return new ResponseEntity<>(portableResponse, HttpStatus.OK);
    }

    @PostMapping("/sendOtp")
    public ResponseEntity<EmailResponse> sendOTP(@RequestBody EmailPayload emailPayload,
                                                 @RequestParam(required = false) String userNameBased,
                                                 @RequestParam(required = false) Boolean isRegistration) {
        try {
            Long userId = null;
            EmailResponse emailResponse = emailPayload.validateEmailPayload(emailPayload);
            if (emailResponse.hasErrors())
                return new ResponseEntity<>(emailResponse, HttpStatus.OK);

            if (userNameBased != null) {
                UserCredentials userCredentials = userDetailsService.getUserCredentials(emailPayload.getTo().get(0));
                if (userCredentials == null) {
                    return new ResponseEntity<>(new EmailResponse().setMessage("User Not Found").
                            setStatus(PortableConstants.FAILED).setStatusCode(404L), HttpStatus.NOT_FOUND);
                }
                userId = userCredentials.getUserID();
            }

            return new ResponseEntity<>(otpValidationService.sendOTPToEmail(emailPayload, userId,
                    BooleanUtils.isTrue(isRegistration)), HttpStatus.OK);
        } catch (BadRequestException badRequestException) {
            return new ResponseEntity<>(new EmailResponse().setMessage("Already User Exists").setStatusCode(200L).setStatus(PortableConstants.FAILED)
                    , HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/validateOtp")
    public ResponseEntity<?> sendOTP(@RequestParam String otp) {
        return new ResponseEntity<>(otpValidationService.isValidOtp(otp), HttpStatus.OK);
    }

    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> passwordDetails) {
        PortableResponse portableResponse = userDetailsService.updatePasswordDetails(passwordDetails);
        return new ResponseEntity<>(portableResponse, portableResponse.getHttpCode(portableResponse.getStatusCode()));
    }

    @PostMapping("/notify-administrator")
    public ResponseEntity<?> notifyAdministrator(@RequestBody Map<String, String> notifyDetails) {
        PortableResponse portableResponse = userDetailsService.notifyAdministrator(notifyDetails);
        return new ResponseEntity<>(portableResponse, portableResponse.getHttpCode(portableResponse.getStatusCode()));
    }

    @PostMapping("/user-registered")
    public ResponseEntity<?> userRegistered(@RequestBody Map<String, String> notifyDetails) {
        PortableResponse portableResponse = userDetailsService.userRegistered(notifyDetails);
        return new ResponseEntity<>(portableResponse, portableResponse.getHttpCode(portableResponse.getStatusCode()));
    }

    @PostMapping("/register-user-data")
    public ResponseEntity<?> registerUserData(@RequestBody Map<String, String> notifyDetails) {
        PortableResponse portableResponse = userDetailsService.registerUserData(notifyDetails);
        return new ResponseEntity<>(portableResponse, portableResponse.getHttpCode(portableResponse.getStatusCode()));
    }

    @PostMapping("/get-last-logged-time")
    public ResponseEntity<?> getLastLoggedTime() {
        PortableResponse portableResponse = userDetailsService.getLastLoggedTime();
        return new ResponseEntity<>(portableResponse, portableResponse.getHttpCode(portableResponse.getStatusCode()));
    }

    @PostMapping("get-user-name")
    public ResponseEntity<?> getUserName() {
        String loggedInUserName = CommonUtils.getLoggedInUserName();
        return new ResponseEntity<>(new PortableResponse("",
                StatusCodes.C_200, PortableConstants.SUCCESS,
                Base64.getEncoder().encode(loggedInUserName.getBytes())), HttpStatus.OK);
    }


}
