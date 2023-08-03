package com.portabull.generic.models;


import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "master_image_uri", schema = DatabaseSchema.PORTABULL_GENERIC, catalog = DatabaseSchema.PORTABULL_GENERIC)
public class MasterImageURI implements Serializable {

    @Id
    @Column(name = "master_uri_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_mstr_img_uri")
    private Long masterURIID;

    @Column(name = "image_name")
    private String imageName;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "description")
    private String description;

    @Column(name = "purpose")
    private String purpose;


    public Long getMasterURIID() {
        return masterURIID;
    }

    public void setMasterURIID(Long masterURIID) {
        this.masterURIID = masterURIID;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}
