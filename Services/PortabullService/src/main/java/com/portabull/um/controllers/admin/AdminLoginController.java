package com.portabull.um.controllers.admin;

import com.portabull.cache.CacheUtils;
import com.portabull.constants.LoggerErrorConstants;
import com.portabull.constants.MessageConstants;
import com.portabull.constants.PortableConstants;
import com.portabull.payloads.AuthenticationRequest;
import com.portabull.response.AuthenticationResponse;
import com.portabull.response.PortableResponse;
import com.portabull.um.UserCredentials;
import com.portabull.um.services.MyUserDetailsService;
import com.portabull.um.utils.JwtUtil;
import com.portabull.utils.commonutils.CommonUtils;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("UM/admin")
public class AdminLoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private MyUserDetailsService userDetailsService;

    Logger logger = LoggerFactory.getLogger(AdminLoginController.class);


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

            final String jwt = jwtTokenUtil.generateToken(userCredentials, true);


            if (userCredentials.getMfaLoginType() != null && userCredentials.getMfaLoginType() == 1) {
                randomToken = userDetailsService.saveJwtDetailsToNotify(jwt, userCredentials);
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
        } catch (Exception e) {
            logger.error(LoggerErrorConstants.EXCEPTION_OCCURRED, e);
            return CommonUtils.returnResponse(new PortableResponse(MessageConstants.SERVICE_FAILED,
                    500L, PortableConstants.FAILED, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> passwordDetails) {
        PortableResponse portableResponse = userDetailsService.updatePasswordDetails(passwordDetails);
        return new ResponseEntity<>(portableResponse, portableResponse.getHttpCode(portableResponse.getStatusCode()));
    }

    @PostMapping("get-user-reports")
    public ResponseEntity<?> getUserReports(@RequestBody Map<String, String> details) {
        PortableResponse portableResponse = userDetailsService.getUserReports();
        return new ResponseEntity<>(portableResponse, portableResponse.getHttpCode(portableResponse.getStatusCode()));
    }

    @PostMapping("unlock-user-account")
    public ResponseEntity<?> unlockUserAccount(@RequestBody Map<String, Object> details) {
        PortableResponse portableResponse = userDetailsService.unlockUserAccount(details.get("id"));
        return new ResponseEntity<>(portableResponse, portableResponse.getHttpCode(portableResponse.getStatusCode()));
    }

    @PostMapping("lock-unlock-account")
    public ResponseEntity<?> lockUnlockAccount(@RequestBody Map<String, Object> details) {
        PortableResponse portableResponse = userDetailsService.lockUnlockAccount(details.get("userName"), details.get("serviceType"));
        return new ResponseEntity<>(portableResponse, portableResponse.getHttpCode(portableResponse.getStatusCode()));
    }

    @PostMapping("lock-unlock-complete-users")
    public ResponseEntity<?> lockUnlockCompleteUsers(@RequestBody Map<String, Object> details) {
        Integer flag = Integer.valueOf(details.get("flag").toString());
        PortableResponse portableResponse = userDetailsService.lockUnlockCompleteUsers(flag);
        return new ResponseEntity<>(portableResponse, portableResponse.getHttpCode(portableResponse.getStatusCode()));
    }

    @PostMapping("/block-log-in-users")
    public ResponseEntity<?> blockLoginUsers() {

        CacheUtils.store(PortableConstants.BLOCK_LOG_INS, true);

        return new ResponseEntity<>(new PortableResponse(), HttpStatus.OK);

    }

    @PostMapping("/un-block-log-in-users")
    public ResponseEntity<?> unBlockLoginUsers() {

        CacheUtils.store(PortableConstants.BLOCK_LOG_INS, false);

        return new ResponseEntity<>(new PortableResponse(), HttpStatus.OK);

    }


}
