package com.portabull.um.controllers;

import com.portabull.response.PortableResponse;
import com.portabull.um.services.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/UM/profile")
public class UserProfileController {

    @Autowired
    UserProfileService userProfileService;

    @GetMapping("/get-profile-picture")
    public ResponseEntity<?> getProfilePicture() throws Exception {

        PortableResponse response = userProfileService.getProfilePicture();

        return new ResponseEntity<>(response, PortableResponse.getHttpCode(response.getStatusCode()));

    }

    @GetMapping("/get-user-profile-info")
    public ResponseEntity<?> getUserProfileInfo() throws Exception {

        PortableResponse response = userProfileService.getUserProfileInfo();

        return new ResponseEntity<>(response, PortableResponse.getHttpCode(response.getStatusCode()));

    }

    @PostMapping("/save-user-profile-info")
    public ResponseEntity<?> saveUserProfileInfo(@RequestBody Map<String, Object> payload) throws Exception {

        PortableResponse response = userProfileService.saveUserProfileInfo(payload);

        return new ResponseEntity<>(response, PortableResponse.getHttpCode(response.getStatusCode()));

    }


    @PostMapping("/upload-profile-picture")
    public ResponseEntity<?> uploadProfilePicture(@RequestParam(value = "file") MultipartFile file) throws IOException {

        PortableResponse response = userProfileService.uploadProfilePicture(file);

        return new ResponseEntity<>(response, PortableResponse.getHttpCode(response.getStatusCode()));

    }

    @PostMapping("/update-twostep-verification")
    public ResponseEntity<?> updateTwoStepVerification(@RequestParam(required = false) Boolean twoStep,
                                                       @RequestParam(required = false) Boolean singleSignIn,
                                                       @RequestParam(required = false) Integer mfa,
                                                       @RequestParam(required = false) Integer sessionExpiredTime) throws IOException {

        PortableResponse response = userProfileService.updateTwoStepVerification(twoStep, singleSignIn, mfa, sessionExpiredTime);

        return new ResponseEntity<>(response, PortableResponse.getHttpCode(response.getStatusCode()));

    }

    @PostMapping("get-user-names")
    public ResponseEntity<?> getUserNames(@RequestBody Map<String, Object> payload) {

        PortableResponse response = userProfileService.getUserNames(payload);

        return new ResponseEntity<>(response, PortableResponse.getHttpCode(response.getStatusCode()));

    }

    @PostMapping("get-user-ids")
    public ResponseEntity<?> getUserIds(@RequestBody List<String> payload) {

        PortableResponse response = userProfileService.getUserIds(payload);

        return new ResponseEntity<>(response, PortableResponse.getHttpCode(response.getStatusCode()));

    }

}
