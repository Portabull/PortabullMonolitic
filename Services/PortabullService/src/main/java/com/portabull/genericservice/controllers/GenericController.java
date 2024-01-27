package com.portabull.genericservice.controllers;

import com.portabull.constants.LoggerErrorConstants;
import com.portabull.constants.MessageConstants;
import com.portabull.constants.PortableConstants;
import com.portabull.execption.BadRequestException;
import com.portabull.genericservice.service.GenericEmailService;
import com.portabull.genericservice.service.GenericService;
import com.portabull.loggerutils.LoggerUtils;
import com.portabull.payloads.EmailPayload;
import com.portabull.response.DocumentResponse;
import com.portabull.response.EmailResponse;
import com.portabull.response.FileResponse;
import com.portabull.response.PortableResponse;
import com.portabull.utils.DownloadUtils;
import com.portabull.utils.JsonUtils;
import com.portabull.utils.QRCodeGenerator;
import com.portabull.utils.validationutils.FileValidationUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("gs")
public class GenericController {

    @Autowired
    GenericEmailService emailService;

    @Autowired
    QRCodeGenerator qrCodeGenerator;

    @Autowired
    GenericService genericService;

    Logger logger = LoggerUtils.getLogger(GenericController.class);


    @PostMapping("/sendOtp")
    public ResponseEntity<EmailResponse> sendOTP(@RequestBody EmailPayload emailPayload) {
        EmailResponse emailResponse = emailPayload.validateEmailPayload(emailPayload);
        if (emailResponse.hasErrors())
            return new ResponseEntity<>(emailResponse, HttpStatus.OK);

        return new ResponseEntity<>(emailService.sendOTPToEmail(emailPayload), HttpStatus.OK);
    }

    @GetMapping("/generate-qr-code")
    public ResponseEntity<?> download(@RequestParam String text) {
        try {
            FileResponse fileResponse = qrCodeGenerator.generateQRCodeImage(text);

            if (PortableConstants.FAILED.equalsIgnoreCase(fileResponse.getStatus()))
                return new ResponseEntity<>(fileResponse, HttpStatus.INTERNAL_SERVER_ERROR);

            return DownloadUtils.download(fileResponse.getFileName(),
                    fileResponse.getMediaType(),
                    fileResponse.getInputStreamResource());
        } catch (Exception e) {
            logger.error("While downloading the document throws error", e);
            return new ResponseEntity<>(new DocumentResponse(MessageConstants.DOWNLOADING_FAILED,
                    500L, PortableConstants.FAILED, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/read-qr-code")
    public ResponseEntity<DocumentResponse> upload(@RequestParam(value = "file") MultipartFile file) {
        try {
            Map<String, Object> response;
            DocumentResponse validationResponse = FileValidationUtils.validate(file);

            if (validationResponse.containErrors()) {
                return new ResponseEntity<>(validationResponse, HttpStatus.BAD_REQUEST);
            }

            String data = qrCodeGenerator.readQRCode(file);
            response = new LinkedHashMap<>();
            response.put("data", data);
            response.put("status", PortableConstants.SUCCESS);
            response.put("statusCode", 200);
            return new ResponseEntity(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("While uploading the document throws error", e);
            return new ResponseEntity<>(new DocumentResponse("Something went wrong please try after sometime",
                    500L, PortableConstants.FAILED, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/compose-mail")
    public ResponseEntity<EmailResponse> composeMail(@RequestPart(name = "files", required = false) List<MultipartFile> files,
                                                     @RequestPart(name = "emailPaylod", required = true) String payload) {
        try {
            EmailPayload emailPayload = JsonUtils.processData(payload, EmailPayload.class);
            EmailResponse emailResponse = emailPayload.validateEmailPayload(emailPayload);
            if (emailResponse.hasErrors())
                return new ResponseEntity<>(emailResponse, HttpStatus.BAD_REQUEST);

            return new ResponseEntity<>(emailService.sendEmail(files, emailPayload), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(LoggerErrorConstants.EXCEPTION_OCCURRED, e);
            return new ResponseEntity<>(new EmailResponse()
                    .setMessage(MessageConstants.SERVICE_FAILED)
                    .setStatus(PortableConstants.FAILED)
                    .setStatusCode(500L), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/send-otp-to-mobile")
    public ResponseEntity<PortableResponse> sendOTPToMobile(@RequestBody Map<String, String> payload, @RequestParam(required = false) boolean whatsApp) {
        if (CollectionUtils.isEmpty(payload) || StringUtils.isEmpty(payload.get("mobileNumber"))) {
            return new ResponseEntity<>(new PortableResponse("Empty Mobile Number",
                    400L, PortableConstants.FAILED, null), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(emailService.sendOTPToMobile(payload.get("mobileNumber"), whatsApp), HttpStatus.OK);
    }

    @PostMapping("/save-client-contact-details")
    public ResponseEntity<PortableResponse> saveClientContactDetails(@RequestBody Map<String, String> payload) {
        return new ResponseEntity<>(genericService.saveClientContactDetails(payload), HttpStatus.OK);
    }

    @GetMapping("/get-client-contact-details")
    public ResponseEntity<PortableResponse> getClientContactDetails() {
        return new ResponseEntity<>(genericService.getClientContactDetails(), HttpStatus.OK);
    }

    @PostMapping("proxy/call-rest-service")
    public ResponseEntity<PortableResponse> callRestService(@RequestBody Map<String, Object> payload) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        if (StringUtils.isEmpty(payload.get("url"))) {
            throw new BadRequestException("Url Should not be empty");
        }

        if (StringUtils.isEmpty(payload.get("method"))) {
            throw new BadRequestException("Method Should not be empty");
        }

        return new ResponseEntity<>(genericService.callRestService(payload), HttpStatus.OK);

    }


}
