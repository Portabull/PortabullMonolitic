package com.portabull.payloads;

public class MISMailAuditPayload {

    private Long mailID;

    private Long misID;

    private String misMailData;

    public Long getMailID() {
        return mailID;
    }

    public void setMailID(Long mailID) {
        this.mailID = mailID;
    }

    public Long getMisID() {
        return misID;
    }

    public void setMisID(Long misID) {
        this.misID = misID;
    }

    public String getMisMailData() {
        return misMailData;
    }

    public void setMisMailData(String misMailData) {
        this.misMailData = misMailData;
    }


}
