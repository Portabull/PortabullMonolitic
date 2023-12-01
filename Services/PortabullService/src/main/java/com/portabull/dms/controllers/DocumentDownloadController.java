package com.portabull.dms.controllers;

import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.dms.service.DocumentService;
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
    public ResponseEntity<?> download(@RequestParam String elocation) throws IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, InvalidKeyException, ClassNotFoundException {

        DocumentResponse documentResponse = documentService.downloadDocument(elocation);

        if (PortableConstants.FAILED.equalsIgnoreCase(documentResponse.getStatus()))
            return new ResponseEntity<>(documentResponse, PortableResponse.getHttpCode(documentResponse.getStatusCode()));

        return DownloadUtils.download(documentResponse.getFileResponse().getFileName(),
                MediaType.parseMediaType(DMSFileUtils.getContentType(documentResponse.getFileResponse().getFileName())),
                documentResponse.getFileResponse().getInputStreamResource());

    }

    @GetMapping("/downloadMultipleFiles")
    public ResponseEntity<?> download(@RequestBody List<String> elocations) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, IOException, InvalidKeyException, ClassNotFoundException {
        DocumentResponse documentResponse = documentService.downloadDocuments(elocations);

        if (PortableConstants.FAILED.equalsIgnoreCase(documentResponse.getStatus()))
            return new ResponseEntity<>(documentResponse, PortableResponse.getHttpCode(documentResponse.getStatusCode()));

        return DownloadUtils.download(documentResponse.getFileResponse().getFileName(),
                MediaType.parseMediaType(DMSFileUtils.getContentType(documentResponse.getFileResponse().getFileName())),
                documentResponse.getFileResponse().getInputStreamResource());

    }

    @PostMapping("/view-documents")
    public ResponseEntity<?> viewDocuments(@RequestParam(required = false) Integer pageNo, @RequestParam(required = false) Integer resultSize) {
        return new ResponseEntity<>(documentService.getDocumentsForUser(pageNo, resultSize), HttpStatus.OK);
    }

    @GetMapping(value = "/play", produces = "video/mp4")
    public Mono<Resource> getVideos(@RequestParam(name = "request") String documentId, @RequestHeader("Range") String range) throws IOException, IllegalBlockSizeException,
            NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return documentService.getVideo(documentId);
    }


    @GetMapping("/download-documents")
    public ResponseEntity<?> download(@RequestParam(required = false) Long documentId,
                                      @RequestParam(required = false) Long folderId) throws IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, InvalidKeyException, ClassNotFoundException {

        String contentType = "";

        DocumentResponse documentResponse;
        if (folderId != null) {
            documentResponse = documentService.downloadFolder(folderId);
        } else {
            documentResponse = documentService.downloadDocument(documentId);
        }

        if (PortableConstants.FAILED.equalsIgnoreCase(documentResponse.getStatus()))
            return new ResponseEntity<>(documentResponse, PortableResponse.getHttpCode(documentResponse.getStatusCode()));

        Map<String, Object> fileResponse = new HashMap<>();

        if (documentResponse.getFileResponse().getFileName().endsWith(".pdf")) {
            contentType = MediaType.APPLICATION_PDF_VALUE;
        }

        fileResponse.put("file", "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(IOUtils.toByteArray(documentResponse.getFileResponse().getInputStream())));

        fileResponse.put("fileName", documentResponse.getFileResponse().getFileName());

        return new ResponseEntity<>(new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, fileResponse), HttpStatus.OK);

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
