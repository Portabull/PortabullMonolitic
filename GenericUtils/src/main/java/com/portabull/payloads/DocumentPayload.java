package com.portabull.payloads;

public class DocumentPayload {

    private Long sno;

    private String fileName;

    private String size;

    private String uploadedTime;

    private String eLocation;

    private String download;

    private String userName;

    private Long documentId;

    public DocumentPayload() {

    }

    public DocumentPayload(Long sno, String fileName, String size, String uploadedTime, String eLocation, String download) {
        this.sno = sno;
        this.fileName = fileName;
        this.size = size;
        this.uploadedTime = uploadedTime;
        this.eLocation = eLocation;
        this.download = download;
    }

    public Long getSno() {
        return sno;
    }

    public void setSno(Long sno) {
        this.sno = sno;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getUploadedTime() {
        return uploadedTime;
    }

    public void setUploadedTime(String uploadedTime) {
        this.uploadedTime = uploadedTime;
    }

    public String geteLocation() {
        return eLocation;
    }

    public void seteLocation(String eLocation) {
        this.eLocation = eLocation;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }
}
