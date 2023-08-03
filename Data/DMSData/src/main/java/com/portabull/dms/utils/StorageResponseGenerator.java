package com.portabull.dms.utils;

import com.portabull.constants.PortableConstants;
import com.portabull.dbutils.HibernateUtils;
import com.portabull.entitys.Document;
import com.portabull.response.DocumentResponse;
import com.portabull.response.FileResponse;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Component
public class StorageResponseGenerator {

    @Autowired
    HibernateUtils hibernateUtils;

    static Logger logger = LoggerFactory.getLogger(StorageResponseGenerator.class);

    public DocumentResponse generateDocumentResponse(String eLocation) {
        DocumentResponse documentResponse = new DocumentResponse();
        Document document;
        try (Session session = hibernateUtils.getSession()) {
            document = (Document) session.createQuery(" FROM Document WHERE eLocation =: eLocation").setParameter("eLocation", eLocation).uniqueResult();
        }
        if (document != null) {
            documentResponse.setDocumentID(document.getId());
        }
        return documentResponse;
    }

    public static DocumentResponse prepareDocumentResponse(String message, Long statusCode, String status, FileResponse fileResponse) {
        return new DocumentResponse(message, statusCode, status, null).setFileResponse(fileResponse);
    }

    public static DocumentResponse prepareDocumentResponse(String message, Long statusCode, String status, Object data) {
        return new DocumentResponse(message, statusCode, status, data);
    }

    public static DocumentResponse prepareDocumentResponse(String message, Long statusCode, String status) {
        return new DocumentResponse(message, statusCode, status, null);
    }

    public static FileResponse prepareFileResponse(File file, boolean insertBytes) throws IOException {
        return new FileResponse().setFile(file).
                setFileName(file.getName()).
                setAbsolutePath(file.getAbsolutePath()).
                setMessage("Success").
                setStatus(PortableConstants.SUCCESS).setBytes(insertBytes ? Files.readAllBytes(file.toPath()) : null);
    }

    public static FileResponse prepareFileResponse(InputStream inputStream) throws IOException {
        return new FileResponse().
                setInputStream(inputStream).
                setStatus(PortableConstants.SUCCESS);
    }


}
