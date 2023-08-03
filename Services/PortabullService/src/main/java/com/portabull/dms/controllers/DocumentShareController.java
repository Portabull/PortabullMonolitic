package com.portabull.dms.controllers;

import com.portabull.constants.MessageConstants;
import com.portabull.constants.PortableConstants;
import com.portabull.dms.service.DocumentService;
import com.portabull.payloads.EmailPayload;
import com.portabull.response.DocumentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("DMS")
public class DocumentShareController {

    @Autowired
    DocumentService documentService;

    static final Logger logger = LoggerFactory.getLogger(DocumentShareController.class);

    @GetMapping("/share")
    public ResponseEntity<DocumentResponse> share(@RequestParam String elocation, @RequestBody EmailPayload emailPayload) {
        try {
            return new ResponseEntity<>(documentService.shareDocument(elocation, emailPayload),
                    HttpStatus.OK);
        } catch (Exception e) {
            logger.error("While sharing the document throws error", e);
            return new ResponseEntity<>(new DocumentResponse(MessageConstants.DOCUMENT_SHARE_FAILED,
                    500L, PortableConstants.FAILED, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
