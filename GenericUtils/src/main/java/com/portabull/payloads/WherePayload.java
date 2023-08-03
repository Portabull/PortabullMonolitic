package com.portabull.payloads;

public class WherePayload {

    private Long whereID;

    private Long userID;

    private String where;

    private Object defaultValue;

    private Long misID;

    public Long getMisID() {
        return misID;
    }

    public void setMisID(Long misID) {
        this.misID = misID;
    }

    public Long getWhereID() {
        return whereID;
    }

    public void setWhereID(Long whereID) {
        this.whereID = whereID;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }
}
