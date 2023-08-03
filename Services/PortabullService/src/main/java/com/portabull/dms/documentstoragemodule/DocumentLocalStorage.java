package com.portabull.dms.documentstoragemodule;

import com.portabull.constants.LoggerErrorConstants;
import com.portabull.constants.MessageConstants;
import com.portabull.constants.PortableConstants;
import com.portabull.response.DocumentResponse;
import com.portabull.dms.utils.StorageResponseGenerator;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
@Primary
public class DocumentLocalStorage implements DocumentStorageModule {

    static Logger logger = LoggerFactory.getLogger(DocumentLocalStorage.class);

    @Value("${local.storage.location}")
    String localStorageLocation;

    @Autowired
    StorageResponseGenerator storageResponseGenerator;

    @Override
    public DocumentResponse uploadDocument(InputStream inputStream, String fileName) throws IOException {

        File dir = new File(localStorageLocation);
        if (!dir.exists()) {
            dir.mkdirs();
            logger.info("Directory Created");
        }

        try (OutputStream outputStream = new FileOutputStream(localStorageLocation + File.separator + fileName)) {
            outputStream.write(IOUtils.toByteArray(inputStream));
        }

        return new DocumentResponse(MessageConstants.DOCUMENT_UPLOAD_SUCCESS, 200L,
                PortableConstants.SUCCESS, null);
    }

    @Override
    public DocumentResponse uploadDocument(InputStream file, String folderName, String fileName) throws IOException {

        File dir = new File(localStorageLocation);
        if (!dir.exists()) {
            dir.mkdirs();
            logger.info("Directory Created");
        }

        File folderDir = new File(localStorageLocation + File.separator + folderName);
        if (!folderDir.exists()) {
            folderDir.mkdirs();
            logger.info("Folder Created");
        }

        try (OutputStream outputStream = new FileOutputStream(localStorageLocation + File.separator + folderName + File.separator + fileName)) {
            outputStream.write(IOUtils.toByteArray(file));
        }

        return new DocumentResponse(MessageConstants.DOCUMENT_UPLOAD_SUCCESS, 200L,
                PortableConstants.SUCCESS, null);
    }

    @Override
    public DocumentResponse downloadDocument(String eLocation) {
        File file = new File(localStorageLocation + File.separator + eLocation);

        if (!file.exists()) {
            return StorageResponseGenerator.prepareDocumentResponse(
                    MessageConstants.FILE_NOT_FOUND, 404L, PortableConstants.SUCCESS);
        }


        try {
            return storageResponseGenerator.generateDocumentResponse(eLocation).
                    setMessage(MessageConstants.DOWNLOAD_SUCCESS).setStatus(PortableConstants.SUCCESS).
                    setFileResponse(StorageResponseGenerator.prepareFileResponse(file, false));
        } catch (Exception e) {
            logger.error(LoggerErrorConstants.EXCEPTION_OCCURRED, e);
            return StorageResponseGenerator.prepareDocumentResponse(
                    MessageConstants.DOWNLOADING_FAILED, 500L, PortableConstants.FAILED);
        }

    }

    @Override
    public DocumentResponse deleteDocument(String eLocation) {
        File file = new File(localStorageLocation + File.separator + eLocation);

        if (!file.exists()) {
            return StorageResponseGenerator.prepareDocumentResponse(
                    MessageConstants.FILE_NOT_FOUND, 404L, PortableConstants.SUCCESS);
        }


        return file.delete() ? storageResponseGenerator.generateDocumentResponse(eLocation).
                setMessage(MessageConstants.DOCUMENT_DELETE_SUCCESS).setStatus(PortableConstants.SUCCESS) :
                storageResponseGenerator.generateDocumentResponse(eLocation).
                        setMessage("File Deleted Failed").setStatus(PortableConstants.FAILED);
    }

    @Override
    public DocumentResponse downloadDocumentBytes(String eLocation) {
        File file = new File(eLocation);
        if (!file.exists()) {
            return StorageResponseGenerator.prepareDocumentResponse(
                    "File Not Found", 404L, PortableConstants.SUCCESS);
        }


        try {
            return storageResponseGenerator.generateDocumentResponse(eLocation).
                    setMessage(MessageConstants.DOWNLOAD_SUCCESS).setStatus(PortableConstants.SUCCESS).
                    setFileResponse(StorageResponseGenerator.prepareFileResponse(file, true));
        } catch (Exception e) {
            logger.error(LoggerErrorConstants.EXCEPTION_OCCURRED, e);
            return StorageResponseGenerator.prepareDocumentResponse(
                    MessageConstants.DOWNLOADING_FAILED, 500L, PortableConstants.FAILED);
        }

    }

    @Override
    public DocumentResponse transferDocument() {
        return new DocumentResponse();
    }


}
