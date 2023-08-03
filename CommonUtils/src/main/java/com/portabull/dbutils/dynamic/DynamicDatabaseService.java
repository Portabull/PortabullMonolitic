package com.portabull.dbutils.dynamic;

import com.portabull.payloads.db.DatabaseEntity;

import java.util.List;
import java.util.Map;

public interface DynamicDatabaseService {

    public boolean createTable(DatabaseEntity databaseEntity);

    public List<Map<String, Object>> getRecordsWithPagination(String entityName, int pageNo, int pageSize);

    public List<Map<String, Object>> getCompleteRecords(String entityName);

    public Map<String, Object> findEntityById(Long id, String entityName);

}
