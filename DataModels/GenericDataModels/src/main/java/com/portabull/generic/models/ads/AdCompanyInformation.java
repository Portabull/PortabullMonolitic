package com.portabull.generic.models.ads;


import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;

@Entity
@Table(name = "ad_company_information", schema = DatabaseSchema.PORTABULL_GENERIC, catalog = DatabaseSchema.PORTABULL_GENERIC)
public class AdCompanyInformation {

    @Id
    @Column(name = "client_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_ad_company_info_id")
    private Long clientId;


    @Column(name = "company_name", unique = true)
    private String companyName;

    @Column(name = "company_biography", columnDefinition = "TEXT")
    private String companyBiography;

    @Column(name = "company_email")
    private String companyEmail;

    @Column(name = "company_mobile")
    private String companyMobile;

    @Column(name = "company_address1")
    private String companyAddress1;

    @Column(name = "company_address2")
    private String companyAddress2;

    @Column(name = "company_city")
    private String companyCity;

    @Column(name = "company_state")
    private String companyState;

    @Column(name = "company_zip")
    private String companyZip;

    @Column(name = "company_logo", columnDefinition = "TEXT")
    private String companyLogo;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyBiography() {
        return companyBiography;
    }

    public void setCompanyBiography(String companyBiography) {
        this.companyBiography = companyBiography;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public String getCompanyMobile() {
        return companyMobile;
    }

    public void setCompanyMobile(String companyMobile) {
        this.companyMobile = companyMobile;
    }

    public String getCompanyAddress1() {
        return companyAddress1;
    }

    public void setCompanyAddress1(String companyAddress1) {
        this.companyAddress1 = companyAddress1;
    }

    public String getCompanyAddress2() {
        return companyAddress2;
    }

    public void setCompanyAddress2(String companyAddress2) {
        this.companyAddress2 = companyAddress2;
    }

    public String getCompanyCity() {
        return companyCity;
    }

    public void setCompanyCity(String companyCity) {
        this.companyCity = companyCity;
    }

    public String getCompanyState() {
        return companyState;
    }

    public void setCompanyState(String companyState) {
        this.companyState = companyState;
    }

    public String getCompanyZip() {
        return companyZip;
    }

    public void setCompanyZip(String companyZip) {
        this.companyZip = companyZip;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }
}
