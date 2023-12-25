package com.portabull.generic.models.ads;

import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ad_activation_history", schema = DatabaseSchema.PORTABULL_GENERIC, catalog = DatabaseSchema.PORTABULL_GENERIC)
public class AdActivationHistory {

    @Id
    @Column(name = "activation_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_ad_activation_id")
    private Long activationId;

    @Column(name = "activate_desc")
    private String activateDesc;

    @Column(name = "activate_reason")
    private String activateReason;

    @Column(name = "activate")
    private boolean activate;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    public Long getActivationId() {
        return activationId;
    }

    public void setActivationId(Long activationId) {
        this.activationId = activationId;
    }

    public String getActivateDesc() {
        return activateDesc;
    }

    public void setActivateDesc(String activateDesc) {
        this.activateDesc = activateDesc;
    }

    public String getActivateReason() {
        return activateReason;
    }

    public void setActivateReason(String activateReason) {
        this.activateReason = activateReason;
    }

    public boolean isActivate() {
        return activate;
    }

    public void setActivate(boolean activate) {
        this.activate = activate;
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
