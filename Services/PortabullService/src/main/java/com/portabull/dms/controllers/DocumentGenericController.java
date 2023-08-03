package com.portabull.dms.controllers;

import com.portabull.constants.MessageConstants;
import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.dms.service.DocumentNotesService;
import com.portabull.dms.service.DocumentService;
import com.portabull.payloads.ReturnReponseCode;
import com.portabull.response.DocumentResponse;
import com.portabull.response.PortableResponse;
import com.portabull.utils.DownloadUtils;
import com.portabull.utils.validationutils.DMSFileUtils;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("DMS")
public class DocumentGenericController {

    @Autowired
    DocumentService documentService;

    @Autowired
    DocumentNotesService notesService;

    static final Logger logger = LoggerFactory.getLogger(DocumentGenericController.class);

    @GetMapping("/delete")
    public ResponseEntity<DocumentResponse> delete(@RequestParam String elocation) {
        try {
            return new ResponseEntity<>(documentService.deleteDocument(elocation), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("While deleting the document throws error", e);
            return new ResponseEntity<>(new DocumentResponse(MessageConstants.DELETE_FAILED, 500L, PortableConstants.FAILED, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/notes/update-notes")
    public ResponseEntity<?> updateNotes(@RequestBody Map<String, String> payload) {

        if (!BooleanUtils.isTrue(Boolean.valueOf(payload.get("delete")))) {
            if (StringUtils.isEmpty(payload.get("note")) || StringUtils.isEmpty(payload.get("tittle"))) {
                return new ResponseEntity<>(new PortableResponse("Note/Tittle should not be empty", StatusCodes.C_400, PortableConstants.FAILED, null), HttpStatus.BAD_REQUEST);
            }
        }

        PortableResponse response = notesService.updateNotes(payload.get("noteId"), payload.get("tittle"), payload.get("note"), BooleanUtils.isTrue(Boolean.valueOf(payload.get("delete"))));

        return new ResponseEntity<>(response, ReturnReponseCode.getHttpStatusCode(response.getStatusCode().intValue()));

    }


    @PostMapping("/notes/get-notes")
    public ResponseEntity<?> getNotes() {

        PortableResponse response = notesService.getNotes();

        return new ResponseEntity<>(response, ReturnReponseCode.getHttpStatusCode(response.getStatusCode().intValue()));

    }

    @PostMapping("/notes/download-notes")
    public ResponseEntity<?> downloadNotes(@RequestBody Map<String, String> payload) throws IOException {

        String noteId = payload.get("noteId");

        if (StringUtils.isEmpty(noteId)) {
            return new ResponseEntity<>(new PortableResponse("noteId is mandatory", StatusCodes.C_400, PortableConstants.FAILED, null), HttpStatus.BAD_REQUEST);
        }

        DocumentResponse response = notesService.downloadNotes(Long.valueOf(noteId));

        return DownloadUtils.download(response.getFileResponse().getFileName(), MediaType.parseMediaType(DMSFileUtils.getContentType(response.getFileResponse().getFileName())), response.getFileResponse().getInputStreamResource());

    }

    @PostMapping("/notes/settings")
    public ResponseEntity<?> notesSettings(@RequestBody Map<String, String> payload) throws IOException {
        PortableResponse response;

        String serviceCode = payload.get("serviceCode");

        switch (serviceCode) {
            case "SHARE"://disable/enable share notes
                response = notesService.enableDisableShareNotes(Boolean.valueOf(payload.get("enableDisableShareNotes")));
                break;
            case "ED-HIDE"://show/hide notes
                response = notesService.enableDisableHiddenNotes(Boolean.valueOf(payload.get("enableDisableHiddenNotes")));
                break;
            case "ED-HIDE-SINGLE"://show/hide notes
                response = notesService.enableDisableSingleHiddenNotes(Boolean.valueOf(payload.get("enableDisableHiddenNotes")), Long.valueOf(payload.get("noteId")));
                break;
            case "ED-DELETE-B":
                response = notesService.hideUnHideDeleteButton(Boolean.valueOf(payload.get("hideUnHideDeleteButton")));
                break;
            default://hide or unhidden the current complete notes
                response = notesService.hideUnHideCompleteNotes(Boolean.valueOf(payload.get("hideUnHideCompleteNotes")));
        }

        return new ResponseEntity<>(response, ReturnReponseCode.getHttpStatusCode(response.getStatusCode().intValue()));

    }

    @PostMapping("/notes/get-settings")
    public ResponseEntity<?> getNotesSettings() throws IOException {
        PortableResponse response;


        response = notesService.getNotesSettings();


        return new ResponseEntity<>(response, ReturnReponseCode.getHttpStatusCode(response.getStatusCode().intValue()));

    }

    @PostMapping("/notes/get-notes-share-details")
    public ResponseEntity<?> getNotesShareDetails(@RequestBody Map<String, Object> payload) {
        PortableResponse response;

        if (StringUtils.isEmpty(payload.get("noteId"))) {

            return new ResponseEntity<>(new PortableResponse("Mandatory fields are missing"
                    , StatusCodes.C_200, PortableConstants.FAILED, null), HttpStatus.BAD_REQUEST);

        }

        response = notesService.getNotesShareDetails(Long.valueOf(payload.get("noteId").toString()));


        return new ResponseEntity<>(response, ReturnReponseCode.getHttpStatusCode(response.getStatusCode().intValue()));

    }

    @PostMapping("/notes/share-notes-details")
    public ResponseEntity<?> shareNotesDetails(@RequestBody Map<String, Object> payload) {
        PortableResponse response;

        if (StringUtils.isEmpty(payload.get("noteId")) || StringUtils.isEmpty(payload.get("userName"))) {

            return new ResponseEntity<>(new PortableResponse("Mandatory fields are missing"
                    , StatusCodes.C_200, PortableConstants.FAILED, null), HttpStatus.BAD_REQUEST);

        }

        response = notesService.shareNotesDetails(payload);


        return new ResponseEntity<>(response, ReturnReponseCode.getHttpStatusCode(response.getStatusCode().intValue()));

    }

    @PostMapping("/notes/check-user-exists")
    public ResponseEntity<?> checkUserExists(@RequestBody Map<String, Object> payload) {

        PortableResponse response = notesService.checkUserExists(payload);

        return new ResponseEntity<>(response, ReturnReponseCode.getHttpStatusCode(response.getStatusCode().intValue()));

    }

    @PostMapping("/notes/remove-share-access")
    public ResponseEntity<?> removeShareAccess(@RequestBody Map<String, Object> payload) {

        PortableResponse response = notesService.removeShareAccess(payload);

        return new ResponseEntity<>(response, ReturnReponseCode.getHttpStatusCode(response.getStatusCode().intValue()));

    }


    @PostMapping("/notes/accept-reject-notification")
    public ResponseEntity<?> acceptRejectNotification(@RequestBody Map<String, Object> payload) {
        PortableResponse response;

        response = notesService.acceptRejectNotification(Long.valueOf(payload.get("sni").toString()),
                Boolean.valueOf(payload.get("acceptFlag").toString()));

        return new ResponseEntity<>(response, ReturnReponseCode.getHttpStatusCode(response.getStatusCode().intValue()));

    }

    @PostMapping("/notes/get-note")
    public ResponseEntity<?> getNotes(@RequestBody Map<String, Object> payload) {
        PortableResponse response;

        if (StringUtils.isEmpty(payload.get("noteId"))) {
            return new ResponseEntity<>(new PortableResponse("", StatusCodes.C_200, PortableConstants.FAILED, null), HttpStatus.BAD_REQUEST);
        }

        response = notesService.getNotes(Long.valueOf(payload.get("noteId").toString()));


        return new ResponseEntity<>(response, ReturnReponseCode.getHttpStatusCode(response.getStatusCode().intValue()));

    }

    @PostMapping("/notes/check-notes-count")
    public ResponseEntity<?> checkNotesCount() {

        PortableResponse response;

        response = notesService.checkNotesCount();

        return new ResponseEntity<>(response, ReturnReponseCode.getHttpStatusCode(response.getStatusCode().intValue()));

    }

    @PostMapping("/notes/get-notifications")
    public ResponseEntity<?> getNotifications() {

        PortableResponse response;

        response = notesService.getNotifications();

        return new ResponseEntity<>(response, ReturnReponseCode.getHttpStatusCode(response.getStatusCode().intValue()));

    }



}
