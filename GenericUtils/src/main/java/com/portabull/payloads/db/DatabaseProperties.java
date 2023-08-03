package com.portabull.payloads.db;

public class DatabaseProperties {

    private String columnName;

    private String dataType;

    private Long minLength;

    private Long maxLength;

    private boolean isMandatory;

    private boolean isUniqueValue;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Long getMinLength() {
        return minLength == null ? 0 : minLength;
    }

    public void setMinLength(Long minLength) {
        this.minLength = minLength;
    }

    public Long getMaxLength() {
        return maxLength == null ? 0 : maxLength;
    }

    public void setMaxLength(Long maxLength) {
        this.maxLength = maxLength;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean mandatory) {
        isMandatory = mandatory;
    }

    public boolean isUniqueValue() {
        return isUniqueValue;
    }

    public void setUniqueValue(boolean uniqueValue) {
        isUniqueValue = uniqueValue;
    }
}
