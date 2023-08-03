package com.portabull.generic.models;

import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;

@Entity
@Table(name = "user_document_storage", schema = DatabaseSchema.PORTABULL_GENERIC, catalog = DatabaseSchema.PORTABULL_GENERIC)
public class UserDocumentStorage {

    @Id
    @Column(name = "storage_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_doc_storage_id")
    private Long storageId;

    @Column(name = "user_id" ,unique = true)
    private Long userId;

    @Column(name = "storage_size")
    private Double storageSize;

    @Column(name = "user_storage_size")
    private Double userStorageSize;

    public Long getStorageId() {
        return storageId;
    }

    public void setStorageId(Long storageId) {
        this.storageId = storageId;
    }

    public Double getStorageSize() {
        return storageSize;
    }

    public void setStorageSize(Double storageSize) {
        this.storageSize = storageSize;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getUserStorageSize() {
        return userStorageSize;
    }

    public void setUserStorageSize(Double userStorageSize) {
        this.userStorageSize = userStorageSize;
    }

}
