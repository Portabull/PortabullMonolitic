package com.portabull.entitys;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;
import java.io.Serializable;
import java.security.Key;
import java.util.Map;

@Entity
@Table(name = "default_document_security", schema = DatabaseSchema.DMS, catalog = DatabaseSchema.DMS)
public class DefaultDocumentSecurity implements Serializable {

    static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
    }

    @Id
    @Column(name = "default_security_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_document_security")
    private Long defaultSecurityID;

    @Column(name = "default_key", nullable = false)
    private String defaultKey;

    public Long getDefaultSecurityID() {
        return defaultSecurityID;
    }

    public void setDefaultSecurityID(Long defaultSecurityID) {
        this.defaultSecurityID = defaultSecurityID;
    }

    public String getDefaultKey() {
        return defaultKey;
    }

    public void setDefaultKey(String defaultKey) {
        this.defaultKey = defaultKey;
    }

    public Key getDefaultSecrectKey() throws JsonProcessingException {
        DefaultDocumentSecurity defaultDocumentSecurity = this;

        Map<String, Object> keyInformation = objectMapper.readValue(defaultDocumentSecurity.getDefaultKey(), Map.class);

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
