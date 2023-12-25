package com.portabull.generic.models.ads;

import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ad_company_tool_subscriptions", schema = DatabaseSchema.PORTABULL_GENERIC, catalog = DatabaseSchema.PORTABULL_GENERIC)

public class AdCompanyToolSubscriptions {


    @Id
    @Column(name = "subscription_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_ad_subscription_id")
    private Long subscriptionId;

    @OneToOne
    @JoinColumn(name = "clientId")
    @MapsId
    private AdCompanyInformation adCompanyInformation;

    @OneToOne
    @JoinColumn(name = "toolId")
    @MapsId
    private AdPortabullTools portabullTools;

    @OneToOne
    @JoinColumn(name = "paymentId")
    @MapsId
    private AdPaymentDetails adPaymentDetails;

    @OneToOne
    @JoinColumn(name = "activationId")
    @MapsId
    private AdActivationHistory adActivationHistory;

    @OneToOne
    @JoinColumn(name = "masterId")
    @MapsId
    private AdCompanyMaster adCompanyMaster;

    @Column(name = "activation_last_date")
    private Date activationLastDate;

    @Column(name = "activation_time_in_days")
    private Long activationTimeInDays;

    @Column(name = "active")
    private boolean active;

    @Column(name = "deactivate_reason")
    private boolean deactivateReason;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public AdCompanyInformation getAdCompanyInformation() {
        return adCompanyInformation;
    }

    public void setAdCompanyInformation(AdCompanyInformation adCompanyInformation) {
        this.adCompanyInformation = adCompanyInformation;
    }

    public AdPortabullTools getPortabullTools() {
        return portabullTools;
    }

    public void setPortabullTools(AdPortabullTools portabullTools) {
        this.portabullTools = portabullTools;
    }

    public AdPaymentDetails getAdPaymentDetails() {
        return adPaymentDetails;
    }

    public void setAdPaymentDetails(AdPaymentDetails adPaymentDetails) {
        this.adPaymentDetails = adPaymentDetails;
    }

    public AdActivationHistory getAdActivationHistory() {
        return adActivationHistory;
    }

    public void setAdActivationHistory(AdActivationHistory adActivationHistory) {
        this.adActivationHistory = adActivationHistory;
    }

    public AdCompanyMaster getAdCompanyMaster() {
        return adCompanyMaster;
    }

    public void setAdCompanyMaster(AdCompanyMaster adCompanyMaster) {
        this.adCompanyMaster = adCompanyMaster;
    }

    public Date getActivationLastDate() {
        return activationLastDate;
    }

    public void setActivationLastDate(Date activationLastDate) {
        this.activationLastDate = activationLastDate;
    }

    public Long getActivationTimeInDays() {
        return activationTimeInDays;
    }

    public void setActivationTimeInDays(Long activationTimeInDays) {
        this.activationTimeInDays = activationTimeInDays;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isDeactivateReason() {
        return deactivateReason;
    }

    public void setDeactivateReason(boolean deactivateReason) {
        this.deactivateReason = deactivateReason;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }
}
