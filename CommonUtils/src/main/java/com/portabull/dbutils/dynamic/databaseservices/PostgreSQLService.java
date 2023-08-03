package com.portabull.dbutils.dynamic.databaseservices;

import com.portabull.dbutils.dynamic.DynamicDatabaseService;
import com.portabull.payloads.db.DatabaseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
@Primary
public class PostgreSQLService extends DatabaseUtilService implements DynamicDatabaseService {

    static Logger logger = LoggerFactory.getLogger(PostgreSQLService.class);

    @Override
    public boolean createTable(DatabaseEntity databaseEntity) {

        String query = databaseEntity.buildPostgresQueryBuilder().generateCreateQuery();

        logger.info(query);

        createTable(query);

        return true;
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
