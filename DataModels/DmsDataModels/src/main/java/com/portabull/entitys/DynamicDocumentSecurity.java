package com.portabull.entitys;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;
import java.io.Serializable;
import java.security.Key;
import java.util.Map;

@Entity
@Table(name = "dynamic_document_security", schema = DatabaseSchema.DMS, catalog = DatabaseSchema.DMS)
public class DynamicDocumentSecurity implements Serializable {

    static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
    }

    @Id
    @Column(name = "dynamic_security_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_document_security")
    private Long dynamicSecurityID;

    @Column(name = "dynamic_key", nullable = false)
    private String dynamicKey;

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static void setObjectMapper(ObjectMapper objectMapper) {
        DynamicDocumentSecurity.objectMapper = objectMapper;
    }

    public Long getDynamicSecurityID() {
        return dynamicSecurityID;
    }

    public void setDynamicSecurityID(Long dynamicSecurityID) {
        this.dynamicSecurityID = dynamicSecurityID;
    }

    public String getDynamicKey() {
        return dynamicKey;
    }

    public void setDynamicKey(String dynamicKey) {
        this.dynamicKey = dynamicKey;
    }

    public Key getDynamicSecrectKey() throws JsonProcessingException {
        DynamicDocumentSecurity dynamicDocumentSecurity = this;

        Map<String, Object> keyInformation = objectMapper.readValue(dynamicDocumentSecurity.getDynamicKey(), Map.class);

        return new Key() {
            @Override
            public String getAlgorithm() {
                return keyInformation.get("algorithm").toString();
            }

            @Override
            public String getFormat() {
                return keyInformation.get("format").toString();
            }

            @Override
            public byte[] getEncoded() {
                return keyInformation.get("encoded").toString().getBytes();
            }
        };

    }


}
