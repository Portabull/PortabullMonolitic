package com.portabull.entitys;

import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_directory", schema = DatabaseSchema.DMS, catalog = DatabaseSchema.DMS)
public class UserDirectory {

    @Id
    @Column(name = "dir_id")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_dir_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    private Long id;

    @Column(name = "directory_name")
    private String directoryName;

    @ManyToOne
    @JoinColumn(name = "parent_dir_id")
    private UserDirectory parentDirectory;

    @Column(name = "dir_level")
    private Integer dirLevel;

    @Column(name = "root_dir")
    private Boolean rootLevel;

    @Column(name = "dir_created_date")
    private Date dirCreatedDate;

    @Column(name = "user_id", nullable = false)
    private Long userID;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public UserDirectory getParentDirectory() {
        return parentDirectory;
    }

    public void setParentDirectory(UserDirectory parentDirectory) {
        this.parentDirectory = parentDirectory;
    }


    public Integer getDirLevel() {
        return dirLevel;
    }

    public void setDirLevel(Integer dirLevel) {
        this.dirLevel = dirLevel;
    }

    public Boolean getRootLevel() {
        return rootLevel;
    }

    public void setRootLevel(Boolean rootLevel) {
        this.rootLevel = rootLevel;
    }

    public Date getDirCreatedDate() {
        return dirCreatedDate;
    }

    public void setDirCreatedDate(Date dirCreatedDate) {
        this.dirCreatedDate = dirCreatedDate;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
