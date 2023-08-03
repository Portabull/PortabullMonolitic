package com.portabull.dms.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portabull.dbutils.HibernateUtils;
import com.portabull.entitys.DefaultDocumentSecurity;
import com.portabull.entitys.DynamicDocumentSecurity;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

@Configuration
@Component
public class EncriptionConfiguration {

    static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
    }


    DefaultDocumentSecurity defaultDocumentSecurity;

    boolean encryptFilesWithSeperateKey;

    @Value("${encrypt.files.with.seperate.key}")
    public void setEncryptFilesWithSeperateKey(boolean seperateKey) {
        encryptFilesWithSeperateKey = seperateKey;
    }

    /**
     * it will prepare default document security
     *
     * @param hibernateUtils
     * @return
     * @throws NoSuchAlgorithmException
     * @throws JsonProcessingException
     */

    @Bean
    public DefaultDocumentSecurity prepareDefaultDocumentSecurity(HibernateUtils hibernateUtils) throws NoSuchAlgorithmException, JsonProcessingException {
        try (Session session = hibernateUtils.getSession()) {
            defaultDocumentSecurity = (DefaultDocumentSecurity) session.createQuery(" FROM DefaultDocumentSecurity").uniqueResult();
            if (defaultDocumentSecurity == null) {
                DefaultDocumentSecurity documentSecurity = new DefaultDocumentSecurity();
                documentSecurity.setDefaultKey(objectMapper.writeValueAsString(getKey()));
                defaultDocumentSecurity = hibernateUtils.saveOrUpdateEntity(documentSecurity);
            }
        }
        return defaultDocumentSecurity;
    }

    /**
     * generate the key here
     *
     * @return
     * @throws NoSuchAlgorithmException
     */

    public Key getKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        return keyGenerator.generateKey();
    }

    /**
     * Encrypting the File bytes here
     *
     * @param content
     * @param secretKey
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public byte[] encryptFile(byte[] content, Key secretKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(content);
    }

    /**
     * Decrypting the File bytes here
     *
     * @param encryptFile
     * @param secretKey
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */

    public byte[] decryptFile(byte[] encryptFile, Key secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(encryptFile);
    }

    /**
     * It will return dynamic document security
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws JsonProcessingException
     */

    public DynamicDocumentSecurity getDynamicDocumentSecurity() throws NoSuchAlgorithmException, JsonProcessingException {
        DynamicDocumentSecurity dynamicDocumentSecurity = new DynamicDocumentSecurity();
        dynamicDocumentSecurity.setDynamicKey(objectMapper.writeValueAsString(getKey()));
        return dynamicDocumentSecurity;
    }

    /**
     * It will return default document security
     *
     * @return
     */

    public DefaultDocumentSecurity getDefaultDocumentSecurity() {
        return defaultDocumentSecurity;
    }


}