package com.portabull.dms.controllers;


import com.portabull.constants.*;
import com.portabull.dms.utils.DocumentOperationUtils;
import com.portabull.response.DocumentResponse;
import com.portabull.response.PortableResponse;
import com.portabull.utils.DownloadUtils;
import com.portabull.utils.JsonUtils;
import com.portabull.utils.validationutils.DMSFileUtils;
import com.portabull.utils.validationutils.FileValidationUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("DMS")
public class DocumentOperationsController {

    static Logger logger = LoggerFactory.getLogger(DocumentOperationsController.class);

    @Autowired
    DocumentOperationUtils documentOperationUtils;

    @PostMapping("/createPassword")
    public ResponseEntity<?> createPassword(@RequestParam(value = "file") MultipartFile file, String password, @RequestParam(required = false) boolean directDownload) {
        try {
            DocumentResponse validationResponse = FileValidationUtils.validate(file);
            if (validationResponse.containErrors()) {
                return new ResponseEntity<>(validationResponse, HttpStatus.BAD_REQUEST);
            }

            if (!FileConstants.PDF.equalsIgnoreCase(FilenameUtils.getExtension(file.getOriginalFilename()))) {
                return new ResponseEntity<>(new DocumentResponse(MessageConstants.UPLOAD_TYPE_PDF, 400L, PortableConstants.FAILED, null), HttpStatus.BAD_REQUEST);
            }

            if (StringUtils.isEmpty(password)) {
                return new ResponseEntity<>(new DocumentResponse(MessageConstants.EMPTY_PASSWORD, 400L, PortableConstants.FAILED, null), HttpStatus.BAD_REQUEST);
            }

            DocumentResponse documentResponse = documentOperationUtils.createPassword(file, password);

            if (PortableConstants.FAILED.equalsIgnoreCase(documentResponse.getStatus()))
                return new ResponseEntity<>(documentResponse, PortableResponse.getHttpCode(documentResponse.getStatusCode()));

            if (!directDownload) {
                return new ResponseEntity<>(documentOperationUtils.getBase64Response(documentResponse), HttpStatus.OK);
            }

            return DownloadUtils.download(documentResponse.getFileResponse().getFileName(), MediaType.parseMediaType(DMSFileUtils.getContentType(documentResponse.getFileResponse().getFileName())), documentResponse.getFileResponse().getInputStreamResource());

        } catch (Exception e) {
            logger.error(LoggerErrorConstants.CREATING_PDF_PASSWORD_ERROR, e);
            return new ResponseEntity<>(new DocumentResponse(MessageConstants.UNABLE_TO_CREATE_PASSWORD, 500L, PortableConstants.FAILED, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/removePassword")
    public ResponseEntity<?> removePassword(@RequestParam(value = "file") MultipartFile file, String password,
                                            @RequestParam(required = false) boolean directDownload) {
        try {
            DocumentResponse validationResponse = FileValidationUtils.validate(file);
            if (validationResponse.containErrors()) {
                return new ResponseEntity<>(validationResponse, HttpStatus.BAD_REQUEST);
            }

            if (!FileConstants.PDF.equalsIgnoreCase(FilenameUtils.getExtension(file.getOriginalFilename()))) {
                return new ResponseEntity<>(new DocumentResponse(MessageConstants.UPLOAD_TYPE_PDF, 400L, PortableConstants.FAILED, null), HttpStatus.BAD_REQUEST);
            }

            if (StringUtils.isEmpty(password)) {
                return new ResponseEntity<>(new DocumentResponse(MessageConstants.EMPTY_PASSWORD, 400L, PortableConstants.FAILED, null), HttpStatus.BAD_REQUEST);
            }

            DocumentResponse documentResponse = documentOperationUtils.removePassword(file, password);

            if (PortableConstants.FAILED.equalsIgnoreCase(documentResponse.getStatus()))
                return new ResponseEntity<>(documentResponse, PortableResponse.getHttpCode(documentResponse.getStatusCode()));

            if (!directDownload) {
                return new ResponseEntity<>(documentOperationUtils.getBase64Response(documentResponse), HttpStatus.OK);
            }

            return DownloadUtils.download(documentResponse.getFileResponse().getFileName(), MediaType.parseMediaType(DMSFileUtils.getContentType(documentResponse.getFileResponse().getFileName())), documentResponse.getFileResponse().getInputStreamResource());

        } catch (Exception e) {
            logger.error(LoggerErrorConstants.REMOVING_PDF_PASSWORD_ERROR, e);
            return new ResponseEntity<>(new DocumentResponse(MessageConstants.UNABLE_TO_REMOVE_PASSWORD, 500L, PortableConstants.FAILED, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/createZipFile")
    public ResponseEntity<?> createZipFile(@RequestParam(value = "file") List<MultipartFile> files) {
        try {
            List<MultipartFile> originalFiles = new ArrayList<>();
            if (CollectionUtils.isEmpty(files)) {
                return new ResponseEntity<>(new DocumentResponse("No Files are uploaded", 4040L, PortableConstants.FAILED, null), HttpStatus.BAD_REQUEST);
            }

            files.forEach(file -> {

                if (FileValidationUtils.validate(file).containErrors()) {
                    return;
                }

                if (!FileValidationUtils.isFileIsPdf(file)) {
                    return;
                }

                originalFiles.add(file);
            });

            if (CollectionUtils.isEmpty(originalFiles)) {
                return new ResponseEntity<>(new DocumentResponse("You have uploaded corrupted/exec files, please try with pdf only", 4040L, PortableConstants.FAILED, null), HttpStatus.BAD_REQUEST);
            }

            DocumentResponse documentResponse = documentOperationUtils.compressToZip(originalFiles);

            if (PortableConstants.FAILED.equalsIgnoreCase(documentResponse.getStatus()))
                return new ResponseEntity<>(documentResponse, PortableResponse.getHttpCode(documentResponse.getStatusCode()));

            return DownloadUtils.download(documentResponse.getFileResponse().getFileName(), MediaType.parseMediaType(DMSFileUtils.getContentType(documentResponse.getFileResponse().getFileName())), documentResponse.getFileResponse().getInputStreamResource());
        } catch (Exception e) {
            logger.error("While creating the zip file it throws error", e);
            return new ResponseEntity<>(new DocumentResponse(MessageConstants.UNABLE_TO_COMPRESS_ZIP, 500L, PortableConstants.FAILED, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/combineMultiplePdfs")
    public ResponseEntity<?> combineMultiplePdfs(@RequestParam(value = "file") List<MultipartFile> files) {
        try {

            if (CollectionUtils.isEmpty(files)) {
                return new ResponseEntity<>(new DocumentResponse("No Files are uploaded", 4040L, PortableConstants.FAILED, null), HttpStatus.BAD_REQUEST);
            }

            List<MultipartFile> originalFiles = new ArrayList<>();
            files.forEach(file -> {

                if (FileValidationUtils.validate(file).containErrors()) {
                    return;
                }

                if (!FileValidationUtils.isFileIsPdf(file)) {
                    return;
                }

                originalFiles.add(file);

            });

            if (CollectionUtils.isEmpty(originalFiles)) {
                return new ResponseEntity<>(new DocumentResponse("You have uploaded corrupted/exec files, please try with pdf only", 4040L, PortableConstants.FAILED, null), HttpStatus.BAD_REQUEST);
            }

            DocumentResponse documentResponse = documentOperationUtils.combineMultiplePdfs(originalFiles);

            if (PortableConstants.FAILED.equalsIgnoreCase(documentResponse.getStatus()))
                return new ResponseEntity<>(documentResponse, PortableResponse.getHttpCode(documentResponse.getStatusCode()));

            return DownloadUtils.download(documentResponse.getFileResponse().getFileName(), MediaType.parseMediaType(DMSFileUtils.getContentType(documentResponse.getFileResponse().getFileName())), documentResponse.getFileResponse().getInputStreamResource());
        } catch (Exception e) {
            logger.error("While merging the pdf it throws error", e);
            return new ResponseEntity<>(new DocumentResponse(MessageConstants.UNABLE_TO_MERGE_PDF, 500L, PortableConstants.FAILED, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/splitPdf")
    public ResponseEntity<?> splitPdf(@RequestParam(value = "file") MultipartFile file, @RequestParam String metadata, @RequestParam(required = false) boolean wantFirstPage, @RequestParam(required = false) boolean wantLastPage) {
        try {

            List<Integer> pageNoInSequence = JsonUtils.processData(metadata, List.class);

            DocumentResponse validationResponse = FileValidationUtils.validate(file);

            if (validationResponse.containErrors()) {
                return new ResponseEntity<>(validationResponse, HttpStatus.BAD_REQUEST);
            }

            if (!FileValidationUtils.isFileIsPdf(file)) {
                return new ResponseEntity<>(new DocumentResponse().setMessage(MessageConstants.PDF_FILE_APPLICABLE).setStatus(PortableConstants.FAILED), HttpStatus.BAD_REQUEST);
            }

            DocumentResponse documentResponse = documentOperationUtils.splitPdf(file, pageNoInSequence, wantFirstPage, wantLastPage);

            if (PortableConstants.FAILED.equalsIgnoreCase(documentResponse.getStatus()))
                return new ResponseEntity<>(documentResponse, PortableResponse.getHttpCode(documentResponse.getStatusCode()));

            return DownloadUtils.download(documentResponse.getFileResponse().getFileName(), MediaType.parseMediaType(DMSFileUtils.getContentType(documentResponse.getFileResponse().getFileName())), documentResponse.getFileResponse().getInputStreamResource());
        } catch (Exception e) {
            logger.error("While merging the pdf it throws error", e);
            return new ResponseEntity<>(new DocumentResponse(MessageConstants.UNABLE_TO_MERGE_PDF, 500L, PortableConstants.FAILED, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/encrypt-any-file")
    public ResponseEntity<?> encryptAnyFile(@RequestParam(value = "file") MultipartFile file,
                                            @RequestParam(required = false) Boolean directDownload) throws IOException {

        DocumentResponse documentResponse = documentOperationUtils.encryptAnyFile(file);

        if (PortableConstants.FAILED.equalsIgnoreCase(documentResponse.getStatus()))
            return new ResponseEntity<>(documentResponse, PortableResponse.getHttpCode(documentResponse.getStatusCode()));

        if (BooleanUtils.isFalse(directDownload)) {

            Map<String, Object> fileResponse = new HashMap<>();

            fileResponse.put("file", "data:;base64," +
                    Base64.getEncoder().encodeToString(IOUtils.toByteArray(documentResponse.getFileResponse().getInputStream())));

            fileResponse.put("fileName", documentResponse.getFileResponse().getFileName());

            return new ResponseEntity<>(new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, fileResponse),
                    HttpStatus.OK);
        }

        return DownloadUtils.download(documentResponse.getFileResponse().getFileName(),
                MediaType.parseMediaType(DMSFileUtils.getContentType(documentResponse.getFileResponse().getFileName())),
                documentResponse.getFileResponse().getInputStreamResource());

    }

    @PostMapping("/decrypt-any-file")
    public ResponseEntity<?> decryptAnyFile(@RequestParam(value = "file") MultipartFile file,
                                            @RequestParam(required = false) Boolean directDownload) throws IOException {
        String contentType = "";
        DocumentResponse documentResponse = documentOperationUtils.decryptAnyFile(file);

        if (PortableConstants.FAILED.equalsIgnoreCase(documentResponse.getStatus()))
            return new ResponseEntity<>(documentResponse, PortableResponse.getHttpCode(documentResponse.getStatusCode()));

        if (BooleanUtils.isFalse(directDownload)) {
            Map<String, Object> fileResponse = new HashMap<>();

            if (documentResponse.getFileResponse().getFileName().endsWith(".pdf")) {
                contentType = MediaType.APPLICATION_PDF_VALUE;
            }

            fileResponse.put("file", "data:" + contentType + ";base64," +
                    Base64.getEncoder().encodeToString(IOUtils.toByteArray(documentResponse.getFileResponse().getInputStream())));

            fileResponse.put("fileName", documentResponse.getFileResponse().getFileName());

            return new ResponseEntity<>(new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, fileResponse),
                    HttpStatus.OK);
        }

        return DownloadUtils.download(documentResponse.getFileResponse().getFileName(),
                MediaType.parseMediaType(DMSFileUtils.getContentType(documentResponse.getFileResponse().getFileName())),
                documentResponse.getFileResponse().getInputStreamResource());

    }

    @PostMapping("/create-pdf-password")
    public ResponseEntity<?> createPassword(@RequestBody Map<String, String> data) {
        try {

            String fileName = data.get("fileName");

            String file = data.get("file");

            String password = data.get("password");

            DocumentResponse validationResponse = FileValidationUtils.validate(file, fileName);

            if (validationResponse.containErrors()) {
                return new ResponseEntity<>(validationResponse, HttpStatus.BAD_REQUEST);
            }

            if (!FileConstants.PDF.equalsIgnoreCase(FilenameUtils.getExtension(((File) validationResponse.getData()).getName()))) {
                return new ResponseEntity<>(new DocumentResponse(MessageConstants.UPLOAD_TYPE_PDF, 400L, PortableConstants.FAILED, null), HttpStatus.BAD_REQUEST);
            }

            if (StringUtils.isEmpty(password)) {
                return new ResponseEntity<>(new DocumentResponse(MessageConstants.EMPTY_PASSWORD, 400L, PortableConstants.FAILED, null), HttpStatus.BAD_REQUEST);
            }

            DocumentResponse documentResponse = documentOperationUtils.createPassword((File) validationResponse.getData(), password);

            if (PortableConstants.FAILED.equalsIgnoreCase(documentResponse.getStatus()))
                return new ResponseEntity<>(documentResponse, PortableResponse.getHttpCode(documentResponse.getStatusCode()));

            documentResponse.getFileResponse().setFileName("password_protected_" + fileName);

            return new ResponseEntity<>(documentOperationUtils.getBase64Response(documentResponse), HttpStatus.OK);

        } catch (Exception e) {
            logger.error(LoggerErrorConstants.CREATING_PDF_PASSWORD_ERROR, e);
            return new ResponseEntity<>(new DocumentResponse(MessageConstants.UNABLE_TO_CREATE_PASSWORD, 500L, PortableConstants.FAILED, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/remove-pdf-password")
    public ResponseEntity<?> removePassword(@RequestBody Map<String, String> data) {
        try {

            String fileName = data.get("fileName");

            String file = data.get("file");

            String password = data.get("password");

            DocumentResponse validationResponse = FileValidationUtils.validate(file, fileName);

            if (validationResponse.containErrors()) {
                return new ResponseEntity<>(validationResponse, HttpStatus.BAD_REQUEST);
            }

            if (!FileConstants.PDF.equalsIgnoreCase(FilenameUtils.getExtension(((File) validationResponse.getData()).getName()))) {
                return new ResponseEntity<>(new DocumentResponse(MessageConstants.UPLOAD_TYPE_PDF, 400L, PortableConstants.FAILED, null), HttpStatus.BAD_REQUEST);
            }

            if (StringUtils.isEmpty(password)) {
                return new ResponseEntity<>(new DocumentResponse(MessageConstants.EMPTY_PASSWORD, 400L, PortableConstants.FAILED, null), HttpStatus.BAD_REQUEST);
            }

            DocumentResponse documentResponse = documentOperationUtils.removePassword((File) validationResponse.getData(), password);

            if (PortableConstants.FAILED.equalsIgnoreCase(documentResponse.getStatus()))
                return new ResponseEntity<>(documentResponse, PortableResponse.getHttpCode(documentResponse.getStatusCode()));

            documentResponse.getFileResponse().setFileName("password_removal_" + fileName);

            return new ResponseEntity<>(documentOperationUtils.getBase64Response(documentResponse), HttpStatus.OK);

        } catch (Exception e) {
            logger.error(LoggerErrorConstants.REMOVING_PDF_PASSWORD_ERROR, e);
            return new ResponseEntity<>(new DocumentResponse(MessageConstants.UNABLE_TO_REMOVE_PASSWORD, 500L, PortableConstants.FAILED, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
