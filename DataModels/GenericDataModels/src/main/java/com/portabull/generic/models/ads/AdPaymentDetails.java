package com.portabull.generic.models.ads;

import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ad_payment_details", schema = DatabaseSchema.PORTABULL_GENERIC, catalog = DatabaseSchema.PORTABULL_GENERIC)
public class AdPaymentDetails {


    @Id
    @Column(name = "payment_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_ad_payment_id")
    private Long paymentId;

    @Column(name = "payment_name")
    private String paymentName;


    @Column(name = "payment_desc")
    private String paymentDesc;


    @Column(name = "total_plan_payment_amount")
    private Double totalPlanPaymentAmount;

    @Column(name = "plan_payment_amount_due")
    private Double planPaymentAmountDue;

    @Column(name = "total_subscription_amount")
    private Double totalSubscriptionAmount;

    @Column(name = "payment_created_date")
    private Date paymentCreatedDate;

    @Column(name = "payment_updated_date")
    private Date paymentUpdatedDate;

    @Column(name = "payment_created_by")
    private Long paymentCreatedBy;

    @Column(name = "payment_updated_by")
    private Long paymentUpdatedBy;

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public String getPaymentDesc() {
        return paymentDesc;
    }

    public void setPaymentDesc(String paymentDesc) {
        this.paymentDesc = paymentDesc;
    }

    public Double getTotalPlanPaymentAmount() {
        return totalPlanPaymentAmount;
    }

    public void setTotalPlanPaymentAmount(Double totalPlanPaymentAmount) {
        this.totalPlanPaymentAmount = totalPlanPaymentAmount;
    }

    public Double getPlanPaymentAmountDue() {
        return planPaymentAmountDue;
    }

    public void setPlanPaymentAmountDue(Double planPaymentAmountDue) {
        this.planPaymentAmountDue = planPaymentAmountDue;
    }

    public Double getTotalSubscriptionAmount() {
        return totalSubscriptionAmount;
    }

    public void setTotalSubscriptionAmount(Double totalSubscriptionAmount) {
        this.totalSubscriptionAmount = totalSubscriptionAmount;
    }

    public Date getPaymentCreatedDate() {
        return paymentCreatedDate;
    }

    public void setPaymentCreatedDate(Date paymentCreatedDate) {
        this.paymentCreatedDate = paymentCreatedDate;
    }

    public Date getPaymentUpdatedDate() {
        return paymentUpdatedDate;
    }

    public void setPaymentUpdatedDate(Date paymentUpdatedDate) {
        this.paymentUpdatedDate = paymentUpdatedDate;
    }

    public Long getPaymentCreatedBy() {
        return paymentCreatedBy;
    }

    public void setPaymentCreatedBy(Long paymentCreatedBy) {
        this.paymentCreatedBy = paymentCreatedBy;
    }

    public Long getPaymentUpdatedBy() {
        return paymentUpdatedBy;
    }

    public void setPaymentUpdatedBy(Long paymentUpdatedBy) {
        this.paymentUpdatedBy = paymentUpdatedBy;
    }
}
