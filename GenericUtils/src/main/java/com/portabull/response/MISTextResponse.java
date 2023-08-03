package com.portabull.response;

import java.util.List;
import java.util.Map;

public class MISTextResponse {

    private Map<String, Integer> columnLength;

    private List<Map<String, Object>> queryResult;

    public Map<String, Integer> getColumnLength() {
        return columnLength;
    }

    public void setColumnLength(Map<String, Integer> columnLength) {
        this.columnLength = columnLength;
    }

    public List<Map<String, Object>> getQueryResult() {
        return queryResult;
    }

    public void setQueryResult(List<Map<String, Object>> queryResult) {
        this.queryResult = queryResult;
    }
}
