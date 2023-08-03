package com.portabull.dms.controllers;

import com.portabull.constants.MessageConstants;
import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.dms.service.DocumentService;
import com.portabull.execption.TokenNotFoundException;
import com.portabull.response.DocumentResponse;
import com.portabull.response.PortableResponse;
import com.portabull.utils.DownloadUtils;
import com.portabull.utils.validationutils.DMSFileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("DMS")
public class DocumentDownloadController {

    @Autowired
    DocumentService documentService;

    static final Logger logger = LoggerFactory.getLogger(DocumentDownloadController.class);

    @GetMapping("/download")
    public ResponseEntity<?> download(@RequestParam String elocation) {
        try {
            DocumentResponse documentResponse = documentService.downloadDocument(elocation);

            if (PortableConstants.FAILED.equalsIgnoreCase(documentResponse.getStatus()))
                return new ResponseEntity<>(documentResponse, PortableResponse.getHttpCode(documentResponse.getStatusCode()));

            return DownloadUtils.download(documentResponse.getFileResponse().getFileName(),
                    MediaType.parseMediaType(DMSFileUtils.getContentType(documentResponse.getFileResponse().getFileName())),
                    documentResponse.getFileResponse().getInputStreamResource());
        } catch (Exception e) {
            logger.error("While downloading the document throws error", e);
            return new ResponseEntity<>(new DocumentResponse(MessageConstants.DOWNLOADING_FAILED,
                    500L, PortableConstants.FAILED, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/downloadMultipleFiles")
    public ResponseEntity<?> download(@RequestBody List<String> elocations) {
        try {
            DocumentResponse documentResponse = documentService.downloadDocuments(elocations);

            if (PortableConstants.FAILED.equalsIgnoreCase(documentResponse.getStatus()))
                return new ResponseEntity<>(documentResponse, PortableResponse.getHttpCode(documentResponse.getStatusCode()));

            return DownloadUtils.download(documentResponse.getFileResponse().getFileName(),
                    MediaType.parseMediaType(DMSFileUtils.getContentType(documentResponse.getFileResponse().getFileName())),
                    documentResponse.getFileResponse().getInputStreamResource());
        } catch (Exception e) {
            logger.error("While downloading the document throws error", e);
            return new ResponseEntity<>(new DocumentResponse(MessageConstants.DOWNLOADING_FAILED,
                    500L, PortableConstants.FAILED, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/view-documents")
    public ResponseEntity<?> viewDocuments(@RequestParam(required = false) Integer pageNo, @RequestParam(required = false) Integer resultSize) {
        try {
            return new ResponseEntity<>(documentService.getDocumentsForUser(pageNo, resultSize), HttpStatus.OK);
        } catch (TokenNotFoundException te) {
            return new ResponseEntity<>(new DocumentResponse(te.getMessage(),
                    401L, PortableConstants.FAILED, null), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error("While downloading the document throws error", e);
            return new ResponseEntity<>(new DocumentResponse(MessageConstants.DOWNLOADING_FAILED,
                    500L, PortableConstants.FAILED, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/play", produces = "video/mp4")
    public Mono<Resource> getVideos(@RequestParam(name = "request") String documentId, @RequestHeader("Range") String range) throws IOException, IllegalBlockSizeException,
            NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return documentService.getVideo(documentId);
    }


    @GetMapping("/download-documents")
    public ResponseEntity<?> download(@RequestParam Long documentId) {
        try {
            String contentType = "";
            DocumentResponse documentResponse = documentService.downloadDocument(documentId);

            if (PortableConstants.FAILED.equalsIgnoreCase(documentResponse.getStatus()))
                return new ResponseEntity<>(documentResponse, PortableResponse.getHttpCode(documentResponse.getStatusCode()));

            Map<String, Object> fileResponse = new HashMap<>();

            if (documentResponse.getFileResponse().getFileName().endsWith(".pdf")) {
                contentType = MediaType.APPLICATION_PDF_VALUE;
            }

            fileResponse.put("file", "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(IOUtils.toByteArray(documentResponse.getFileResponse().getInputStream())));

            fileResponse.put("fileName", documentResponse.getFileResponse().getFileName());

            return new ResponseEntity<>(new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, fileResponse), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("While downloading the document throws error", e);
            return new ResponseEntity<>(new DocumentResponse(MessageConstants.DOWNLOADING_FAILED,
                    500L, PortableConstants.FAILED, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("get-dms-files")
    public ResponseEntity<?> getDmsFiles(@RequestBody Map<String, Object> payload) {

        boolean fetchRootDirs = false;

        Long dirId = null;

        String ds = null;

        if (payload.get("fetchRootDirs") != null) {
            fetchRootDirs = Boolean.valueOf(payload.get("fetchRootDirs").toString());
        } else {
            dirId = Long.valueOf(payload.get("dirId").toString());
            ds = payload.get("ds").toString();
        }


        DocumentResponse documentResponse = documentService.getDmsFiles(dirId, fetchRootDirs, ds);

        return new ResponseEntity<>(documentResponse, HttpStatus.OK);

    }


}
