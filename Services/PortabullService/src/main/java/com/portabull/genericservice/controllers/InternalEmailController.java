package com.portabull.genericservice.controllers;

import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.genericservice.service.InternalEmailService;
import com.portabull.response.PortableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("gs/internal-emails")
public class InternalEmailController {

    @Autowired
    InternalEmailService emailService;


    @PostMapping("/send-internal-email")
    public ResponseEntity<?> sendInternalEmail(@RequestBody Map<String, Object> payload) {

        if (payload.get("to") == null) {
            return new ResponseEntity<>(new PortableResponse("Email is mandatory", StatusCodes.C_400, PortableConstants.FAILED, null), HttpStatus.BAD_REQUEST);
        }

        Long userId = Long.valueOf(payload.get("to").toString());

        return new ResponseEntity<>(emailService.sendInternalEmail(payload.get("subject").toString(), payload.get("body").toString(), userId), HttpStatus.OK);
    }

    @PostMapping("/get-internal-emails")
    public ResponseEntity<?> getInternalEmails(@RequestBody Map<String, Object> payload) {
        return new ResponseEntity<>(emailService.getInternalEmails(payload.get("messageType").toString()), HttpStatus.OK);
    }

    @PostMapping("/update-mail-seen")
    public ResponseEntity<?> updateMailSeen(@RequestBody Map<String, Object> payload) {

        List<Long> messageIds = new ArrayList<>();
        if (payload.get("messageIds") == null || CollectionUtils.isEmpty((List<Long>) payload.get("messageIds"))) {
            return new ResponseEntity<>(new PortableResponse("No MailIds",
                    StatusCodes.C_200, PortableConstants.SUCCESS, null), HttpStatus.OK);
        }

        ((List<Object>) payload.get("messageIds")).forEach(messageId -> {
            messageIds.add(Long.valueOf(messageId.toString()));
        });

        return new ResponseEntity<>(emailService.updateMailSeen(messageIds), HttpStatus.OK);
    }

}
