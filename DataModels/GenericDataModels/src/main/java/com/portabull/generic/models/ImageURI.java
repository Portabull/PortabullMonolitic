package com.portabull.generic.models;

import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "image_uri", schema = DatabaseSchema.PORTABULL_GENERIC, catalog = DatabaseSchema.PORTABULL_GENERIC)
public class ImageURI implements Serializable {

    @Id
    @Column(name = "uri_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_img_uri")
    private Long uriID;

    @Column(name = "template_id")
    private Long templateID;

    @OneToOne
    @JoinColumn(name = "masterURIID")
    @MapsId
    private MasterImageURI masterImageURI;

    @Column(name = "replace_url_name")
    private String replaceUrlName;

    public Long getUriID() {
        return uriID;
    }

    public void setUriID(Long uriID) {
        this.uriID = uriID;
    }

    public MasterImageURI getMasterImageURI() {
        return masterImageURI;
    }

    public void setMasterImageURI(MasterImageURI masterImageURI) {
        this.masterImageURI = masterImageURI;
    }

    public String getReplaceUrlName() {
        return replaceUrlName;
    }

    public void setReplaceUrlName(String replaceUrlName) {
        this.replaceUrlName = replaceUrlName;
    }

    public Long getTemplateID() {
        return templateID;
    }

    public void setTemplateID(Long templateID) {
        this.templateID = templateID;
    }
}
