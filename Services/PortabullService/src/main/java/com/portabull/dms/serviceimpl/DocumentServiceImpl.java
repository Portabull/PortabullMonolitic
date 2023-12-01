package com.portabull.dms.serviceimpl;

import com.portabull.constants.PortableConstants;
import com.portabull.dms.configuration.EncriptionConfiguration;
import com.portabull.dms.dao.DocumentDao;
import com.portabull.dms.documentstoragemodule.DocumentStorageModule;
import com.portabull.dms.service.DocumentService;
import com.portabull.dms.utils.DMSUtils;
import com.portabull.dms.utils.DocumentOperationUtils;
import com.portabull.entitys.Document;
import com.portabull.execption.NotFoundException;
import com.portabull.execption.UnAuthorizedException;
import com.portabull.generic.dao.CommonDao;
import com.portabull.generic.models.UserDocumentStorage;
import com.portabull.payloads.DocumentPayload;
import com.portabull.payloads.EmailPayload;
import com.portabull.response.DocumentResponse;
import com.portabull.utils.commonutils.CommonUtils;
import com.portabull.utils.dateutils.DateUtils;
import com.portabull.utils.emailutils.EmailUtils;
import com.portabull.utils.fileutils.FileHandling;
import com.portabull.utils.validationutils.FileValidationUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;
import reactor.core.publisher.Mono;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    DocumentDao documentDao;

    @Autowired
    CommonDao commonDao;

    @Autowired
    DMSUtils dmsUtils;

    @Autowired
    @Qualifier("configureStorageModule")
    DocumentStorageModule documentStorageModule;

    @Autowired
    EmailUtils emailUtils;

    @Autowired
    EncriptionConfiguration encriptionConfiguration;

    @Autowired
    DocumentOperationUtils documentOperationUtils;

    static Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

    /**
     * cashing userId's for sync operation
     */

    List<Long> userIds = new ArrayList<>();

    @Override
    public DocumentResponse uploadDocument(MultipartFile file) throws IOException, NoSuchAlgorithmException, ClassNotFoundException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException, TikaException, SAXException {

        synchronized (getLoggedInUserId()) {

            UserDocumentStorage userDocumentStorage = commonDao.getUserDocumentStorage();

            if (isStorageLimitExceeded(file, userDocumentStorage)) {
                return new DocumentResponse("Storage Capacity Fulled",
                        200L, PortableConstants.FAILED, null, null);
            }

            DocumentResponse validationResponse = FileValidationUtils.validate(file);

            if (validationResponse.containErrors()) {
                return validationResponse;
            }

            return uploadDocumentInternally(file, userDocumentStorage);
        }
    }

    @Override
    public DocumentResponse uploadDocumentInternally(MultipartFile file, UserDocumentStorage userDocumentStorage) throws NoSuchAlgorithmException, IOException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, InvalidKeyException {

        Document document = dmsUtils.generateDocument(file);

        DocumentResponse documentResponse = documentStorageModule.uploadDocument(dmsUtils.invokeEncryption(file, document), document.geteLocation());

        document = documentDao.createDocument(document);

        documentResponse.setDocumentID(document.getId());

        double megabytes = (document.getSize() / 1024.0) / 1024.0;

        userDocumentStorage.setUserStorageSize(userDocumentStorage.getUserStorageSize() + megabytes);

        commonDao.saveOrUpdateEntity(userDocumentStorage);

        return documentResponse;

    }

    @Override
    public DocumentResponse downloadFolder(Long folderId) {



        return null;
    }

    private synchronized Long getLoggedInUserId() {
        Long userId = CommonUtils.getLoggedInUserId();
        Long syncUserId = userIds.stream().filter(syUserId ->
                syUserId.equals(userId)).findAny().orElse(null);

        if (syncUserId == null) {
            syncUserId = new Long(userId);
            userIds.add(syncUserId);
        }
        return syncUserId;
    }

    private boolean isStorageLimitExceeded(MultipartFile file, UserDocumentStorage userDocumentStorage) {

        double megabytes = (file.getSize() / 1024.0) / 1024.0;

        return userDocumentStorage.getStorageSize() <= userDocumentStorage.getUserStorageSize() + megabytes;
    }

    @Override
    @Async
    public CompletableFuture<DocumentResponse> uploadDocumentAsync(MultipartFile file, Long dirId, RequestAttributes requestAttributes) throws
            IOException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException {

        logger.info("Thread Name: {}", Thread.currentThread().getName());

        RequestContextHolder.setRequestAttributes(requestAttributes, true);

        DocumentResponse validationResponse = FileValidationUtils.validate(file);

        if (validationResponse.containErrors()) {
            validationResponse.setData(file.getOriginalFilename());
            return CompletableFuture.completedFuture(validationResponse);
        }

        synchronized (getLoggedInUserId()) {

            UserDocumentStorage userDocumentStorage = commonDao.getUserDocumentStorage();

            if (isStorageLimitExceeded(file, userDocumentStorage)) {
                return CompletableFuture.completedFuture(new DocumentResponse("Storage Capacity Fulled",
                        200L, PortableConstants.FAILED, file.getOriginalFilename(), null));
            }

            double megabytes = (file.getSize() / 1024.0) / 1024.0;

            userDocumentStorage.setUserStorageSize(userDocumentStorage.getUserStorageSize() + megabytes);

            commonDao.saveOrUpdateEntity(userDocumentStorage);

        }

        Document document = dmsUtils.generateDocument(file);

        DocumentResponse documentResponse = documentStorageModule.uploadDocument(dmsUtils.invokeEncryption(file, document), document.geteLocation());

        document = documentDao.createDocument(document);

        documentResponse.setDocumentID(document.getId());

        documentDao.mapFileToDirectory(document, dirId);

        documentResponse.setData(file.getOriginalFilename());

        return CompletableFuture.completedFuture(documentResponse);
    }

    @Override
    public DocumentResponse downloadDocument(String eLocation) throws IllegalBlockSizeException, NoSuchAlgorithmException, IOException, BadPaddingException, NoSuchPaddingException, InvalidKeyException, ClassNotFoundException {

        Document document = checkFilePermissions(eLocation);

        DocumentResponse documentResponse = documentStorageModule.downloadDocument(eLocation);

        InputStream inputStream = dmsUtils.invokeDecryption(documentResponse.getFileResponse().getInputStream(), document);

        documentResponse.getFileResponse().setInputStream(inputStream).setFileName(document.getName());

        return documentResponse;
    }

    @Override
    public DocumentResponse deleteDocument(String eLocation) {
        return documentStorageModule.deleteDocument(eLocation);
    }

    @Override
    public DocumentResponse shareDocument(String eLocation, EmailPayload emailPayload) throws IOException {
        emailUtils.sendEmail(
                EmailUtils.prepareEmailPayload(
                        emailPayload.setAttachment(
                                FileHandling.convertBytesToFile
                                        (
                                                FileHandling.createTempFile
                                                        (
                                                                FilenameUtils.getName
                                                                        (
                                                                                eLocation
                                                                        ),
                                                                FilenameUtils.getExtension
                                                                        (
                                                                                eLocation
                                                                        )
                                                        ),
                                                documentStorageModule.downloadDocumentBytes
                                                                (
                                                                        eLocation
                                                                )
                                                        .getFileResponse().getBytes()
                                        )
                        ),
                        "Document Send By Portabull DMS",
                        EmailUtils.getHtmlText
                                (
                                        "beefree-fo8ob0ro64r.html"
                                ),
                        "", true
                )
        );
        return new DocumentResponse("Successfully shared the document", 200L, PortableConstants.SUCCESS, null);
    }

    @Override
    public DocumentResponse downloadDocuments(List<String> elocations) throws BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, ClassNotFoundException, NoSuchPaddingException, InvalidKeyException, IOException {
        List<DocumentResponse> documentResponses = new ArrayList<>();
        for (String eLocation : elocations) {
            DocumentResponse documentResponse = downloadDocument(eLocation);
            documentResponses.add(documentResponse);
        }

        return documentOperationUtils.compressToZipFile(documentResponses);
    }

    @Override
    public Map<String, Object> getStorage() {
        Map<String, Object> response = new LinkedHashMap<>();
        UserDocumentStorage documentStorageDms = commonDao.getUserDocumentStorage();
        if (documentStorageDms.getUserStorageSize() == 0) {
            response.put("storage", 0);
        } else {
            response.put("storage", (int) ((documentStorageDms.getUserStorageSize() / documentStorageDms.getStorageSize()) * 100));
        }
        return response;
    }

    @Override
    public List<DocumentPayload> getDocumentsForUser(Integer pageNo, Integer resultSize) {
        List<DocumentPayload> viewDocs = new ArrayList<>();
        resultSize = resultSize == null ? 5 : resultSize;
        pageNo = pageNo == null ? 0 : pageNo * resultSize;
        List<Document> documents = documentDao.getDocumentsForUser(pageNo, resultSize);
        if (!CollectionUtils.isEmpty(documents)) {
            AtomicReference<Integer> sNO = new AtomicReference<>(pageNo + 1);
            documents.forEach(document -> {
                DocumentPayload viewDoc = new DocumentPayload();
                viewDoc.setFileName(document.getName());
                viewDoc.setSize(dmsUtils.getFormattedSize(document.getSize()));
                viewDoc.setDownload("download");
                viewDoc.setUploadedTime(DateUtils.formatDate(DateUtils.DD_MM_YYYY_HH_MM_SS, document.getUploadedDate()));
                viewDoc.setSno(sNO.get().longValue());
                viewDoc.setUserName(document.getUserName());
                viewDoc.seteLocation(document.geteLocation());
                viewDoc.setDocumentId(document.getId());
                sNO.set(sNO.get() + 1);
                viewDocs.add(viewDoc);
            });
        }
        return viewDocs;
    }

    @Override
    public Mono<Resource> getVideo(String documentId) throws IOException, IllegalBlockSizeException, NoSuchPaddingException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        Document document = documentDao.getDocument(
                Long.valueOf(new String(Base64.getDecoder().decode(documentId)))
        );

        DocumentResponse documentResponse = documentStorageModule.downloadDocument(document.geteLocation());

        InputStream inputStream = dmsUtils.invokeDecryption(documentResponse.getFileResponse().getInputStream(), document);

        ByteArrayResource byteArrayResource = new ByteArrayResource(IOUtils.toByteArray(inputStream));

        return Mono.fromSupplier(() -> byteArrayResource);
    }


    private Document checkFilePermissions(Document document) {

        if (!CommonUtils.getLoggedInUserId().equals(document.getUserID())) {
            throw new UnAuthorizedException("File Not belongs to you...");
        }

        return document;

    }

    private Document checkFilePermissions(Long documentId) {

        Document document = documentDao.getDocument(documentId);

        if (document == null) {
            throw new NotFoundException("Document Not Found...");
        }

        checkFilePermissions(document);

        return document;

    }

    private Document checkFilePermissions(String eLocation) {

        Document document = documentDao.getDocumentByELocation(eLocation);

        if (document == null) {
            throw new NotFoundException("Document Not Found...");
        }

        checkFilePermissions(document);

        return document;

    }

    @Override
    public DocumentResponse downloadDocument(Long documentId) throws IllegalBlockSizeException, NoSuchAlgorithmException, IOException, BadPaddingException, NoSuchPaddingException, InvalidKeyException, ClassNotFoundException {

        Document document = checkFilePermissions(documentId);

        DocumentResponse documentResponse = documentStorageModule.downloadDocument(document.geteLocation());

        InputStream inputStream = dmsUtils.invokeDecryption(documentResponse.getFileResponse().getInputStream(), document);

        documentResponse.getFileResponse().setInputStream(inputStream).setFileName(document.getName());

        return documentResponse;
    }

    @Override
    public DocumentResponse getDmsFiles(Long dirId, boolean fetchRootDirs, String ds) {

        return documentDao.getDmsFiles(dirId, fetchRootDirs, ds);

    }

    @Override
    public DocumentResponse createDMSDir(String dirName, Integer level, Long parentDirectoryId) {

        return documentDao.createDMSDir(dirName, level, parentDirectoryId);

    }


    @Override
    public DocumentResponse editDirName(Long dirId, String dirName) {
        return documentDao.editDirName(dirId, dirName);
    }

    @Override
    public DocumentResponse deleteDirectory(Long dirId) {
        return documentDao.deleteDirectory(dirId);
    }

    @Override
    public DocumentResponse editFileName(Long fileId, String fileName) {
        return documentDao.editFileName(fileId, fileName);
    }

    @Override
    public DocumentResponse deleteFile(Long fileId) {

        Document document = documentDao.getDocument(fileId);

        if (!document.getUserID().equals(CommonUtils.getLoggedInUserId())) {
            throw new UnAuthorizedException("This file is not belongs to you");
        }

        deleteDocument(document.geteLocation());

        double megabytes = (document.getSize() / 1024.0) / 1024.0;

        UserDocumentStorage userDocumentStorage = commonDao.getUserDocumentStorage();

        userDocumentStorage.setUserStorageSize(userDocumentStorage.getUserStorageSize() - megabytes);

        commonDao.saveOrUpdateEntity(userDocumentStorage);

        return documentDao.deleteFile(document);
    }

}
