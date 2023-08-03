package com.portabull.payloads;

import java.util.Map;

public class MISReportPayload {

    private Long misReportId;

    private String misName;

    private Long userId;

    private String userName;

    private String documentTittle;

    private String headerColourCode;

    private String groupBy;

    private String orderBy;

    private String timeStampFormat;

    private boolean logoRequired;

    private boolean timeStamoRequired;

    private String file;

    private String fileName;

    private String viewName;

    private Map<String, String> selectClause;

    public String getMisName() {
        return misName;
    }

    public void setMisName(String misName) {
        this.misName = misName;
    }

    public String getDocumentTittle() {
        return documentTittle;
    }

    public void setDocumentTittle(String documentTittle) {
        this.documentTittle = documentTittle;
    }

    public String getHeaderColourCode() {
        return headerColourCode;
    }

    public void setHeaderColourCode(String headerColourCode) {
        this.headerColourCode = headerColourCode;
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

    public String getTimeStampFormat() {
        return timeStampFormat;
    }

    public void setTimeStampFormat(String timeStampFormat) {
        this.timeStampFormat = timeStampFormat;
    }

    public boolean isLogoRequired() {
        return logoRequired;
    }

    public void setLogoRequired(boolean logoRequired) {
        this.logoRequired = logoRequired;
    }

    public boolean isTimeStamoRequired() {
        return timeStamoRequired;
    }

    public void setTimeStamoRequired(boolean timeStamoRequired) {
        this.timeStamoRequired = timeStamoRequired;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Map<String, String> getSelectClause() {
        return selectClause;
    }

    public void setSelectClause(Map<String, String> selectClause) {
        this.selectClause = selectClause;
    }

    public Long getMisReportId() {
        return misReportId;
    }

    public void setMisReportId(Long misReportId) {
        this.misReportId = misReportId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
