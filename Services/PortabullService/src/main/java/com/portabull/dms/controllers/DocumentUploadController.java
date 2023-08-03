package com.portabull.dms.controllers;

import com.portabull.constants.LoggerErrorConstants;
import com.portabull.constants.MessageConstants;
import com.portabull.constants.PortableConstants;
import com.portabull.dms.service.DocumentService;
import com.portabull.loggerutils.LoggerUtils;
import com.portabull.response.DocumentResponse;
import com.portabull.response.PortableResponse;
import com.portabull.utils.ThreadPoolConfiguration;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("DMS")
public class DocumentUploadController {

    @Autowired
    DocumentService documentService;

    static final Logger logger = LoggerUtils.getLogger(DocumentUploadController.class);

    @PostMapping("/upload")
    public ResponseEntity<DocumentResponse> upload(@RequestParam(value = "file") MultipartFile file) {
        try {
            return new ResponseEntity<>(documentService.uploadDocument(file), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("While uploading the document throws error", e);
            return new ResponseEntity<>(new DocumentResponse(MessageConstants.UPLOADING_FAILED,
                    500L, PortableConstants.FAILED, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/uploadMultipleFiles")
    public synchronized ResponseEntity<?> upload(@RequestParam(value = "file") List<MultipartFile> files) {
        List<DocumentResponse> documentResponses = new ArrayList<>();
        try {
            AtomicReference<Integer> successCount = new AtomicReference<>(0);
            AtomicReference<Integer> failedCount = new AtomicReference<>(0);

            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            files.forEach(file -> {
                try {
                    documentResponses.add(documentService.uploadDocumentAsync(file, null, requestAttributes).get());
                    successCount.set(successCount.get() + 1);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    logger.error("Interrupted!", ex);
                } catch (Exception e) {
                    failedCount.set(failedCount.get() + 1);
                    logger.error("While uploading the documents it throws error", e);
                    documentResponses.add(new DocumentResponse().
                            setMessage("File Not Uploaded" + file.getOriginalFilename())
                            .setStatus(PortableConstants.FAILED).setStatusCode(500L));
                }
            });
            return new ResponseEntity<>(new PortableResponse(
                    "Files Uploaded Successfully[No of files Uploaded:" + successCount.get() + "][No of files Not Uploaded:" + failedCount.get() + "]",
                    200L, PortableConstants.SUCCESS, documentResponses), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("While uploading the document throws error", e);
            return new ResponseEntity<>(Arrays.asList(new DocumentResponse(MessageConstants.UPLOADING_FAILED,
                    500L, PortableConstants.FAILED, null)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/uploadMultipleFilesAsync")
    public ResponseEntity<?> uploadMultipleFiles(@RequestParam(value = "file") List<MultipartFile> files) {
        List<DocumentResponse> documentResponses = new ArrayList<>();
        try {
            List<Callable<DocumentResponse>> callableTasks = new ArrayList<>();
            ExecutorService executorService = Executors.newFixedThreadPool(ThreadPoolConfiguration.getThreadCountBasedOnNoOfTasks(files.size()));

            AtomicReference<Integer> successCount = new AtomicReference<>(0);
            AtomicReference<Integer> failedCount = new AtomicReference<>(0);
            files.forEach(file -> {
                callableTasks.add(new Callable<DocumentResponse>() {
                    @Override
                    public DocumentResponse call() {
                        try {
                            return documentService.uploadDocument(file);
                        } catch (Exception e) {
                            logger.error(LoggerErrorConstants.EXCEPTION_OCCURRED, e);
                            failedCount.set(failedCount.get() + 1);
                            return new DocumentResponse("File Not Uploaded" + file.getOriginalFilename(),
                                    500L, PortableConstants.FAILED, null, null);
                        }
                    }
                });
            });

            List<Future<DocumentResponse>> futureResponses = executorService.invokeAll(callableTasks);
            for (Future<DocumentResponse> objectFuture : futureResponses) {
                DocumentResponse fileUploadResponse = objectFuture.get();
                if (PortableConstants.SUCCESS.equalsIgnoreCase(fileUploadResponse.getStatus())) {
                    successCount.set(successCount.get() + 1);
                } else {
                    failedCount.set(failedCount.get() + 1);
                }
                documentResponses.add(fileUploadResponse);
            }

            return new ResponseEntity<>(new PortableResponse(
                    successCount.get() + "/" + (successCount.get() + failedCount.get()) + " files are uploaded successfully",
                    200L, PortableConstants.SUCCESS, documentResponses), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("While uploading the document throws error", e);
            return new ResponseEntity<>(Arrays.asList(new DocumentResponse(MessageConstants.UPLOADING_FAILED,
                    500L, PortableConstants.FAILED, null)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/get-storage")
    public ResponseEntity<?> getStorage() {
        try {
            return new ResponseEntity<>(documentService.getStorage(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("While downloading the document throws error", e);
            return new ResponseEntity<>(new DocumentResponse(MessageConstants.DOWNLOADING_FAILED,
                    500L, PortableConstants.FAILED, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create-dms-dir")
    public ResponseEntity<?> createDMSDir(@RequestBody Map<String, Object> payload) {

        String dirName = payload.get("dirName").toString();

        Integer level = Integer.valueOf(payload.get("level").toString());

        Long parentDirectoryId = Long.valueOf(payload.get("parentDirId").toString());

        return new ResponseEntity(documentService.createDMSDir(dirName, level, parentDirectoryId), HttpStatus.OK);

    }

    @PostMapping("/upload-multiple-files-to-dir")
    public synchronized ResponseEntity<?> uploadFiles(@RequestParam(value = "file") List<MultipartFile> files, @RequestParam Long dirId) {
        List<DocumentResponse> documentResponses = new ArrayList<>();
        try {
            AtomicReference<Integer> successCount = new AtomicReference<>(0);
            AtomicReference<Integer> failedCount = new AtomicReference<>(0);

            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            files.forEach(file -> {
                try {
                    DocumentResponse response = documentService.uploadDocumentAsync(file, dirId, requestAttributes).get();
                    if (PortableConstants.FAILED.equals(response.getStatus())) {
                        failedCount.set(failedCount.get() + 1);
                    } else {
                        successCount.set(successCount.get() + 1);
                    }
                    documentResponses.add(response);

                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    logger.error("Interrupted!", ex);

                    failedCount.set(failedCount.get() + 1);
                    documentResponses.add(new DocumentResponse().
                            setMessage("Something went wrong while uploading with this file")
                            .setStatus(PortableConstants.FAILED).setStatusCode(500L).setData(file.getOriginalFilename()));
                } catch (Exception e) {
                    failedCount.set(failedCount.get() + 1);
                    logger.error("While uploading the documents it throws error", e);
                    documentResponses.add(new DocumentResponse().
                            setMessage("Something went wrong while uploading with this file")
                            .setStatus(PortableConstants.FAILED).setStatusCode(500L).setData(file.getOriginalFilename()));
                }
            });
            return new ResponseEntity<>(new PortableResponse(
                    "Files Uploaded Successfully[No of files Uploaded:" + successCount.get() + "][No of files Not Uploaded:" + failedCount.get() + "]",
                    200L, PortableConstants.SUCCESS, documentResponses), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("While uploading the document throws error", e);
            return new ResponseEntity<>(Arrays.asList(new DocumentResponse(MessageConstants.UPLOADING_FAILED,
                    500L, PortableConstants.FAILED, null)), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/modify-dms-file-dir")
    public ResponseEntity<?> modifyDMSFileDir(@RequestBody Map<String, Object> payload) {
        DocumentResponse response;
        String modifyType = payload.get("mt").toString();

        if ("e".equals(modifyType)) {
            Long dirId = Long.valueOf(payload.get("dirId").toString());
            String dirName = payload.get("dirName").toString();

            response = documentService.editDirName(dirId, dirName);
        } else if ("d".equals(modifyType)) {
            Long dirId = Long.valueOf(payload.get("dirId").toString());
            response = documentService.deleteDirectory(dirId);
        } else if ("fe".equals(modifyType)) {
            Long fileId = Long.valueOf(payload.get("fileId").toString());
            String fileName = payload.get("fileName").toString();

            response = documentService.editFileName(fileId, fileName);
        } else {
            Long fileId = Long.valueOf(payload.get("fileId").toString());
            response = documentService.deleteFile(fileId);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
