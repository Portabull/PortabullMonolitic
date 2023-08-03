package com.portabull.payloads;

public class SelectPayload {

    private Long selectId;

    private String selectName;

    private String aliasName;

    private Long misID;

    public Long getMisID() {
        return misID;
    }

    public void setMisID(Long misID) {
        this.misID = misID;
    }


    public Long getSelectId() {
        return selectId;
    }

    public void setSelectId(Long selectId) {
        this.selectId = selectId;
    }

    public String getSelectName() {
        return selectName;
    }

    public void setSelectName(String selectName) {
        this.selectName = selectName;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }
}
