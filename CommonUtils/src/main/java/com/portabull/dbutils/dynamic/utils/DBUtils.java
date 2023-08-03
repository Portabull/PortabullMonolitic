package com.portabull.dbutils.dynamic.utils;

import com.portabull.dbutils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.ResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class DBUtils {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    HibernateUtils hibernateUtils;

    @Autowired
    Environment environment;

    public List<Map<String, Object>> getViewsViaSchemas(String... schemas) {
        int sno = 0;

        List<Map<String, Object>> response = new ArrayList<>();

        List<String> validSchemas = validateSchemas(schemas);

        List<Map<String, Object>> views = jdbcTemplate.queryForList("SELECT table_name,table_schema FROM INFORMATION_SCHEMA.VIEWS where table_schema IN ("
                + String.join(", ", validSchemas) + ")");

        for (Map<String, Object> view : views) {
            sno++;

            Map<String, Object> tableData = new LinkedHashMap<>();

            tableData.put("sno", sno);

            tableData.put("tableName", view.get("table_name").toString());

            tableData.put("schemeName", view.get("table_schema").toString());

            tableData.put("viewName", view.get("table_schema").toString() + "." + view.get("table_name").toString());

            response.add(tableData);
        }

        return response;
    }

    public List<Map<String, Object>> getViews() {
        return getViewsViaSchemas(environment.getProperty("configured.views").split(","));
    }

    private List<String> validateSchemas(String[] schemas) {

        List<String> validatedSchemas = new ArrayList<>();

        for (String schema : schemas) {

            schema = schema.replace("\"", "");

            if (!schema.startsWith("'")) {
                schema = "'" + schema;
            }

            if (!schema.endsWith("'")) {
                schema = schema + "'";
            }

            validatedSchemas.add(schema);

        }

        return validatedSchemas;
    }

    public Object getViewDetails(String viewName) {

        Map<String, Object> response = new LinkedHashMap<>();

        try (Session session = hibernateUtils.getSession()) {

            NativeQuery query = session.createSQLQuery("SELECT * FROM " + viewName).setFirstResult(1)
                    .setMaxResults(1);

            query.setResultTransformer(
                    new ResultTransformer() {
                        @Override
                        public Object transformTuple(Object[] tuple, String[] aliases) {
                            Map<String, Object> result = new LinkedHashMap<>(tuple.length);
                            for (int i = 0; i < tuple.length; i++) {
                                result.put(aliases[i], tuple[i]);
                            }
                            return result;
                        }

                        @Override
                        public List transformList(List collection) {
                            return collection;
                        }
                    }
            );

            List<Map<String, Object>> data = query.list();

            if (CollectionUtils.isEmpty(data)) {
                SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM " + viewName);
                String[] dbColumns = sqlRowSet.getMetaData().getColumnNames();
                response.put("columns", dbColumns);
                return response;
            }
            response.put("columns", data.get(0).keySet());
        }

        return response;
    }
}
