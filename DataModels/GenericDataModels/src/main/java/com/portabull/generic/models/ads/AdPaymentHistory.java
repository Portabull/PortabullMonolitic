package com.portabull.generic.models.ads;

import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ad_payment_history", schema = DatabaseSchema.PORTABULL_GENERIC, catalog = DatabaseSchema.PORTABULL_GENERIC)
public class AdPaymentHistory {

    @Id
    @Column(name = "payment__history_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_ad_payment_history_id")
    private Long paymentHistoryId;

    @OneToOne
    @JoinColumn(name = "paymentId")
    @MapsId
    private AdPaymentDetails adPaymentDetails;

    @Column(name = "payment_amount")
    private Double paymentAmount;

    @Column(name = "payment_history_desc")
    private Double paymentHistoryDesc;

    @Column(name = "payment_history_created_date")
    private Date paymentHistoryCreatedDate;

    @Column(name = "payment_history_updated_date")
    private Date paymentHistoryUpdatedDate;

    @Column(name = "payment_history_created_by")
    private Long paymentHistoryCreatedBy;

    @Column(name = "payment_history_updated_by")
    private Long paymentHistoryUpdatedBy;

    public Long getPaymentHistoryId() {
        return paymentHistoryId;
    }

    public void setPaymentHistoryId(Long paymentHistoryId) {
        this.paymentHistoryId = paymentHistoryId;
    }

    public AdPaymentDetails getAdPaymentDetails() {
        return adPaymentDetails;
    }

    public void setAdPaymentDetails(AdPaymentDetails adPaymentDetails) {
        this.adPaymentDetails = adPaymentDetails;
    }

    public Double getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(Double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public Double getPaymentHistoryDesc() {
        return paymentHistoryDesc;
    }

    public void setPaymentHistoryDesc(Double paymentHistoryDesc) {
        this.paymentHistoryDesc = paymentHistoryDesc;
    }

    public Date getPaymentHistoryCreatedDate() {
        return paymentHistoryCreatedDate;
    }

    public void setPaymentHistoryCreatedDate(Date paymentHistoryCreatedDate) {
        this.paymentHistoryCreatedDate = paymentHistoryCreatedDate;
    }

    public Date getPaymentHistoryUpdatedDate() {
        return paymentHistoryUpdatedDate;
    }

    public void setPaymentHistoryUpdatedDate(Date paymentHistoryUpdatedDate) {
        this.paymentHistoryUpdatedDate = paymentHistoryUpdatedDate;
    }

    public Long getPaymentHistoryCreatedBy() {
        return paymentHistoryCreatedBy;
    }

    public void setPaymentHistoryCreatedBy(Long paymentHistoryCreatedBy) {
        this.paymentHistoryCreatedBy = paymentHistoryCreatedBy;
    }

    public Long getPaymentHistoryUpdatedBy() {
        return paymentHistoryUpdatedBy;
    }

    public void setPaymentHistoryUpdatedBy(Long paymentHistoryUpdatedBy) {
        this.paymentHistoryUpdatedBy = paymentHistoryUpdatedBy;
    }
}
