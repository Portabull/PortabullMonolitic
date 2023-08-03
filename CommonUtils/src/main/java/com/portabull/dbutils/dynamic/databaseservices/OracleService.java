package com.portabull.dbutils.dynamic.databaseservices;

import com.portabull.dbutils.dynamic.DynamicDatabaseService;
import com.portabull.payloads.db.DatabaseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OracleService extends DatabaseUtilService implements DynamicDatabaseService {

    @Override
    public boolean createTable(DatabaseEntity databaseEntity) {
        return false;
    }

    @Override
    public List<Map<String, Object>> getRecordsWithPagination(String entityName, int pageNo, int pageSize) {
        return super.getRecordsWithPagination(entityName, pageNo, pageSize);
    }

    @Override
    public List<Map<String, Object>> getCompleteRecords(String entityName) {
        return super.getCompleteRecords(entityName);
    }

    @Override
    public Map<String, Object> findEntityById(Long id, String entityName) {
        return super.findEntityById(id, entityName);
    }


}
