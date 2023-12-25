package com.portabull.generic.models.ads;

import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;

@Entity
@Table(name = "ad_company_master", schema = DatabaseSchema.PORTABULL_GENERIC, catalog = DatabaseSchema.PORTABULL_GENERIC)
public class AdCompanyMaster {

    @Id
    @Column(name = "company_master_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_ad_company_master_id")
    private Long masterId;

    @Column(name = "ad_desc")
    private String adDesc;

    @Column(name = "meta_data", columnDefinition = "TEXT")
    private String metaData;

    @Column(name = "images", columnDefinition = "TEXT")
    private String images;

    public Long getMasterId() {
        return masterId;
    }

    public void setMasterId(Long masterId) {
        this.masterId = masterId;
    }

    public String getAdDesc() {
        return adDesc;
    }

    public void setAdDesc(String adDesc) {
        this.adDesc = adDesc;
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }
}
