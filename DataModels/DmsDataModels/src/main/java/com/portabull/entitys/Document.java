package com.portabull.entitys;

import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "document_data", schema = DatabaseSchema.DMS, catalog = DatabaseSchema.DMS)
public class Document implements Serializable {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_document")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "size")
    private Long size;

    @Column(name = "e_location", unique = true, nullable = false)
    private String eLocation;

    @Column(name = "user_id", nullable = false)
    private Long userID;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "uploaded_date")
    private Date uploadedDate;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "default_security_id")
    private DefaultDocumentSecurity defaultDocumentSecurity;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "dynamic_security_id")
    private DynamicDocumentSecurity dynamicDocumentSecurity;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "documentId")
    private Set<UserDocumentDirectoryMapping> userDocDirMapping;

    @Column(name = "is_file_deleted")
    private Boolean isDeleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String geteLocation() {
        return eLocation;
    }

    public void seteLocation(String eLocation) {
        this.eLocation = eLocation;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public DefaultDocumentSecurity getDefaultDocumentSecurity() {
        return defaultDocumentSecurity;
    }

    public void setDefaultDocumentSecurity(DefaultDocumentSecurity defaultDocumentSecurity) {
        this.defaultDocumentSecurity = defaultDocumentSecurity;
    }

    public DynamicDocumentSecurity getDynamicDocumentSecurity() {
        return dynamicDocumentSecurity;
    }

    public void setDynamicDocumentSecurity(DynamicDocumentSecurity dynamicDocumentSecurity) {
        this.dynamicDocumentSecurity = dynamicDocumentSecurity;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getUploadedDate() {
        return uploadedDate;
    }

    public void setUploadedDate(Date uploadedDate) {
        this.uploadedDate = uploadedDate;
    }

    public Set<UserDocumentDirectoryMapping> getUserDocDirMapping() {
        return userDocDirMapping;
    }

    public void setUserDocDirMapping(Set<UserDocumentDirectoryMapping> userDocDirMapping) {
        this.userDocDirMapping = userDocDirMapping;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
