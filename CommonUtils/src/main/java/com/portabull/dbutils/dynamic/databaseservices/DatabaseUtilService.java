package com.portabull.dbutils.dynamic.databaseservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

public class DatabaseUtilService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public void createTable(String query) {
        jdbcTemplate.execute(query);
    }

    public List<Map<String, Object>> getRecordsWithPagination(String entityName, int pageNo, int pageSize) {
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM ").append(entityName)
                .append(" LIMIT ").append(pageSize).append(" OFFSET ")
                .append(pageNo * pageSize);
        return jdbcTemplate.queryForList(queryBuilder.toString());
    }

    public List<Map<String, Object>> getCompleteRecords(String entityName) {
        return jdbcTemplate.queryForList(new StringBuilder("SELECT * FROM ").append(entityName).toString());
    }

    public Map<String,Object> findEntityById(Long id,String entityName){
        return jdbcTemplate.queryForMap(new StringBuilder("SELECT * FROM ").append(entityName).toString());
    }

}
