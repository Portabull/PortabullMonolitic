package com.portabull.payloads;

import java.util.Map;

public class PaytmPayload {

    private String merchantId;

    private String merchantKey;

    private String channelId;

    private String website;

    private String industryTypeId;

    private String paytmUrl;

    private String customerId;

    private String transactionAmount;

    private String orderId;

    private Map<String, String> details;

    public PaytmPayload() {
    }


    public PaytmPayload(String merchantId, String merchantKey, String channelId, String website,
                        String industryTypeId, String paytmUrl, Map<String, String> details) {
        super();
        this.merchantId = merchantId;
        this.merchantKey = merchantKey;
        this.channelId = channelId;
        this.website = website;
        this.industryTypeId = industryTypeId;
        this.paytmUrl = paytmUrl;
        this.details = details;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantKey() {
        return merchantKey;
    }

    public void setMerchantKey(String merchantKey) {
        this.merchantKey = merchantKey;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getIndustryTypeId() {
        return industryTypeId;
    }

    public void setIndustryTypeId(String industryTypeId) {
        this.industryTypeId = industryTypeId;
    }

    public String getPaytmUrl() {
        return paytmUrl;
    }

    public void setPaytmUrl(String paytmUrl) {
        this.paytmUrl = paytmUrl;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }


    @Override
    public String toString() {
        return "PaytmDetailPojo [merchantId=" + merchantId + ", merchantKey=" + merchantKey + ", channelId=" + channelId
                + ", website=" + website + ", industryTypeId=" + industryTypeId + ", paytmUrl=" + paytmUrl
                + ", details=" + details + "]";
    }


}
