package com.portabull.entitys;

import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_doc_dir_mapping", schema = DatabaseSchema.DMS, catalog = DatabaseSchema.DMS)
public class UserDocumentDirectoryMapping implements Serializable {

    @Id
    @Column(name = "document_id")
    private Long documentId;

    @Id
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "dir_id")
    private UserDirectory userDirectory;

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public UserDirectory getUserDirectory() {
        return userDirectory;
    }

    public void setUserDirectory(UserDirectory userDirectory) {
        this.userDirectory = userDirectory;
    }

}
