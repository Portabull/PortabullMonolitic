package com.portabull.dms.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portabull.constants.FileConstants;
import com.portabull.dms.configuration.EncriptionConfiguration;
import com.portabull.entitys.Document;
import com.portabull.utils.commonutils.CommonUtils;
import com.portabull.utils.fileutils.FileHandling;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Component
public class DMSUtils {

    static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
    }

    boolean encryptFilesWithSeperateKey;

    @Value("${encrypt.files}")
    boolean isEncryptedFiles;

    @Autowired
    EncriptionConfiguration encriptionConfiguration;

    @Value("${encrypt.files.with.seperate.key}")
    public void setEncryptFilesWithSeperateKey(boolean seperateKey) {
        encryptFilesWithSeperateKey = seperateKey;
    }

    public Document generateDocument(MultipartFile file) throws JsonProcessingException, NoSuchAlgorithmException {
        Document document = new Document();
        document.setUserID(CommonUtils.getLoggedInUserId());
        document.setUserName(CommonUtils.getLoggedInUserName());
        document.seteLocation(FileHandling.prepareRandomFileName(file.getOriginalFilename()));
        document.setSize(file.getSize());
        document.setName(file.getOriginalFilename());
        document.setUploadedDate(new Date());
        if (isEncryptedFiles) {
            if (encryptFilesWithSeperateKey) {
                document.setDynamicDocumentSecurity(encriptionConfiguration.getDynamicDocumentSecurity());
            } else {
                document.setDefaultDocumentSecurity(encriptionConfiguration.getDefaultDocumentSecurity());
            }
        }
        return document;
    }

    public Document generateDocument(MultipartFile file, String folderName) {
        Document document = new Document();
        String randomFileName = FileHandling.prepareRandomFileName(file.getOriginalFilename());
        document.setUserID(CommonUtils.getLoggedInUserIdDummy());
        document.seteLocation(randomFileName);
        document.setSize(file.getSize());
        document.setName(file.getOriginalFilename());
        document.seteLocation(folderName + FileConstants.FILE_SEPARATOR + randomFileName);
        return document;
    }


    public InputStream invokeEncryption(MultipartFile file, Document document) throws IOException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        Key secretKey;
        if (isEncryptedFiles) {
            if (encryptFilesWithSeperateKey) {
                secretKey = document.getDynamicDocumentSecurity().getDynamicSecrectKey();
            } else {
                secretKey = document.getDefaultDocumentSecurity().getDefaultSecrectKey();
            }
            return new ByteArrayInputStream(encriptionConfiguration.encryptFile(file.getBytes(), secretKey));
        } else {
            return file.getInputStream();
        }
    }

    public InputStream invokeDecryption(InputStream file, Document document) throws IOException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        if (document.getDynamicDocumentSecurity() != null) {
            return new ByteArrayInputStream(encriptionConfiguration.decryptFile(IOUtils.toByteArray(file), document.getDynamicDocumentSecurity().getDynamicSecrectKey()));
        } else if (document.getDefaultDocumentSecurity() != null) {
            return new ByteArrayInputStream(encriptionConfiguration.decryptFile(IOUtils.toByteArray(file), document.getDefaultDocumentSecurity().getDefaultSecrectKey()));
        } else {
            return file;
        }
    }

    public String getFormattedSize(Long size) {
        String formattedSize = "";
        double kb = (double) size / FileConstants.KILO;
        double mb = kb / FileConstants.KILO;
        double gb = mb / FileConstants.KILO;
        double tb = gb / FileConstants.KILO;
        double pb = tb / FileConstants.KILO;
        if (size < FileConstants.KILO) {
            formattedSize = size + " Bytes";
        } else if (size >= FileConstants.KILO && size < FileConstants.MEGA) {
            formattedSize = String.format("%.2f", kb) + " KB";
        } else if (size >= FileConstants.MEGA && size < FileConstants.GIGA) {
            formattedSize = String.format("%.2f", mb) + " MB";
        } else if (size >= FileConstants.GIGA && size < FileConstants.TERA) {
            formattedSize = String.format("%.2f", gb) + " GB";
        } else if (size >= FileConstants.TERA && size < FileConstants.PETA) {
            formattedSize = String.format("%.2f", tb) + " TB";
        } else if (size >= FileConstants.PETA) {
            formattedSize = String.format("%.2f", pb) + " PB";
        }
        return formattedSize;
    }

}
