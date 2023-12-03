package com.portabull.dms.service;

import com.portabull.generic.models.UserDocumentStorage;
import com.portabull.payloads.DocumentPayload;
import com.portabull.payloads.EmailPayload;
import com.portabull.response.DocumentResponse;
import org.apache.tika.exception.TikaException;
import org.springframework.core.io.Resource;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;
import reactor.core.publisher.Mono;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface DocumentService {

    public DocumentResponse uploadDocument(MultipartFile file) throws IOException, NoSuchAlgorithmException, ClassNotFoundException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException, TikaException, SAXException;

    public DocumentResponse downloadDocument(String eLocation) throws IllegalBlockSizeException, NoSuchAlgorithmException, IOException, BadPaddingException, NoSuchPaddingException, InvalidKeyException, ClassNotFoundException;

    public DocumentResponse deleteDocument(String eLocation);

    public DocumentResponse shareDocument(String eLocation, EmailPayload emailPayload) throws IOException;

    public CompletableFuture<DocumentResponse> uploadDocumentAsync(MultipartFile file, Long dirId, RequestAttributes requestAttributes) throws IOException, NoSuchAlgorithmException, ClassNotFoundException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException, ExecutionException, InterruptedException, TikaException, SAXException;

    public DocumentResponse downloadDocuments(List<String> elocations) throws BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, ClassNotFoundException, NoSuchPaddingException, InvalidKeyException, IOException;

    public Map<String, Object> getStorage();

    public List<DocumentPayload> getDocumentsForUser(Integer pageNo, Integer resultSize);

    public Mono<Resource> getVideo(String documentId) throws IOException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException;

    public DocumentResponse downloadDocument(Long documentId) throws IllegalBlockSizeException, NoSuchAlgorithmException, IOException, BadPaddingException, NoSuchPaddingException, InvalidKeyException, ClassNotFoundException;

    public DocumentResponse getDmsFiles(Long dirId, boolean fetchRootDirs, String ds);

    public DocumentResponse createDMSDir(String dirName, Integer level, Long parentDirectoryId);

    DocumentResponse editDirName(Long dirId, String dirName);

    DocumentResponse deleteDirectory(Long dirId);

    DocumentResponse editFileName(Long fileId, String fileName);

    DocumentResponse deleteFile(Long fileId);

    DocumentResponse uploadDocumentInternally(MultipartFile file, UserDocumentStorage userDocumentStorage) throws NoSuchAlgorithmException, IOException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, InvalidKeyException;

    DocumentResponse downloadFolder(Long folderId) throws IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, InvalidKeyException, ClassNotFoundException;
}
