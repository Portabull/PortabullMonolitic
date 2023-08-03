package com.portabull.payloads;

import java.util.List;

public class MISPayload {

    private Long misID;

    private Long userID;

    private String userName;

    private String misName;

    private String viewName;

    private List<WherePayload> whereClause;

    private List<SelectPayload> selectClause;

    private String groupBy;

    private String orderBy;

    private boolean isTimeStampRequired;

    private String timeStampFormat;

    private String documentTittle;

    private Long dmsDocumentId;

    private String tempLogoImagePath;

    private boolean isLogoRequired;

    private String headerColorCode;

    private Long downloadCount;

    private Long mailCount;

    private List<MISMailAuditPayload> misMailAudits;

    public Long getMisID() {
        return misID;
    }

    public void setMisID(Long misID) {
        this.misID = misID;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getMisName() {
        return misName;
    }

    public void setMisName(String misName) {
        this.misName = misName;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public List<SelectPayload> getSelectClause() {
        return selectClause;
    }

    public void setSelectClause(List<SelectPayload> selectClause) {
        this.selectClause = selectClause;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public List<WherePayload> getWhereClause() {
        return whereClause;
    }

    public void setWhereClause(List<WherePayload> whereClause) {
        this.whereClause = whereClause;
    }

    public boolean isTimeStampRequired() {
        return isTimeStampRequired;
    }

    public void setTimeStampRequired(boolean timeStampRequired) {
        isTimeStampRequired = timeStampRequired;
    }

    public String getTimeStampFormat() {
        return timeStampFormat;
    }

    public void setTimeStampFormat(String timeStampFormat) {
        this.timeStampFormat = timeStampFormat;
    }

    public String getDocumentTittle() {
        return documentTittle;
    }

    public void setDocumentTittle(String documentTittle) {
        this.documentTittle = documentTittle;
    }

    public String getTempLogoImagePath() {
        return tempLogoImagePath;
    }

    public void setTempLogoImagePath(String tempLogoImagePath) {
        this.tempLogoImagePath = tempLogoImagePath;
    }

    public boolean isLogoRequired() {
        return isLogoRequired;
    }

    public void setLogoRequired(boolean logoRequired) {
        isLogoRequired = logoRequired;
    }

    public String getHeaderColorCode() {
        return headerColorCode;
    }

    public void setHeaderColorCode(String headerColorCode) {
        this.headerColorCode = headerColorCode;
    }

    public Long getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Long downloadCount) {
        this.downloadCount = downloadCount;
    }

    public Long getMailCount() {
        return mailCount;
    }

    public void setMailCount(Long mailCount) {
        this.mailCount = mailCount;
    }

    public List<MISMailAuditPayload> getMisMailAudits() {
        return misMailAudits;
    }

    public void setMisMailAudits(List<MISMailAuditPayload> misMailAudits) {
        this.misMailAudits = misMailAudits;
    }

    public Long getDmsDocumentId() {
        return dmsDocumentId;
    }

    public void setDmsDocumentId(Long dmsDocumentId) {
        this.dmsDocumentId = dmsDocumentId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
