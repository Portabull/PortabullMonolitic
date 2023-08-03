package com.portabull.utils.queryutils;

import com.portabull.payloads.MISPayload;
import com.portabull.payloads.PageLimit;
import com.portabull.utils.validationutils.Validations;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Component
public class QueryGenerator {

    public <T> Query<T> generateDynamicQuery(Session session, String tableName, Map<String, Object> queryParams, List<String> selectedColumns) {

        Validations.removeEmptyValues(selectedColumns);

        Validations.removeNullValues(queryParams);

        StringBuilder queryBuilder = prepareSelectClause(selectedColumns, tableName.trim());

        prepareWhereClause(queryBuilder, queryParams);

        Query<T> query = session.createSQLQuery(queryBuilder.toString());

        applyQueryParameters(query, queryParams);

        return query;
    }

    private StringBuilder prepareSelectClause(List<String> selectedColumns, String tableName) {
        StringBuilder queryBuilder;

        if (CollectionUtils.isEmpty(selectedColumns)) {
            queryBuilder = new StringBuilder("SELECT * FROM ").append(tableName);
        } else {
            queryBuilder = new StringBuilder("SELECT ");
            selectedColumns.forEach(column -> {
                if (!Validations.isStringEmpty(column)) {
                    queryBuilder.append(column.trim()).append(",");
                }
            });
            queryBuilder.deleteCharAt(queryBuilder.length() - 1);
            queryBuilder.append(" FROM ").append(tableName);
        }
        return queryBuilder;
    }

    private void prepareWhereClause(StringBuilder queryBuilder, Map<String, Object> queryParams) {
        if (CollectionUtils.isEmpty(queryParams))
            return;

        queryBuilder.append(" WHERE ");
        queryParams.forEach((queryParamKey, queryParamValue) -> {
            if (queryParamValue instanceof List) {
                queryBuilder.append(queryParamKey).append(" IN (:").append(queryParamKey).append(") AND ");
            } else {
                queryBuilder.append(queryParamKey).append("=:").append(queryParamKey).append(" AND ");
            }
        });

        queryBuilder.setLength(queryBuilder.length() - 5);
    }

    private <T> void applyQueryParameters(Query<T> query, Map<String, Object> queryParams) {
        if (CollectionUtils.isEmpty(queryParams))
            return;

        queryParams.forEach(query::setParameter);

    }

    public <T> Query<T> generateDynamicQuery(Session session, MISPayload misPayload, PageLimit pageLimit) {

        StringBuilder queryBuilder = prepareSelectClause(misPayload);

        prepareWhereClause(queryBuilder, misPayload);

        prepareGroupByAndOrderBy(queryBuilder, misPayload);

        Query<T> query = session.createSQLQuery(queryBuilder.toString());

        applyQueryParameters(query, misPayload);

        applyPageLimit(query, pageLimit);

        return query;
    }

    private <T> void applyPageLimit(Query<T> query, PageLimit pageLimit) {
        if (Validations.isEmptyObject(pageLimit) || !pageLimit.hasPageLimit())
            return;


        query.setFirstResult(pageLimit.getStartPage());
        query.setMaxResults(pageLimit.getMaxResults());
    }

    private void prepareGroupByAndOrderBy(StringBuilder queryBuilder, MISPayload misPayload) {
        if (!Validations.isStringEmpty(misPayload.getGroupBy()))
            queryBuilder.append(" GROUP BY ").append(misPayload.getGroupBy());

        if (!Validations.isStringEmpty(misPayload.getOrderBy()))
            queryBuilder.append(" ORDER BY ").append(misPayload.getOrderBy());
    }

    private StringBuilder prepareSelectClause(MISPayload misPayload) {
        StringBuilder queryBuilder;

        if (CollectionUtils.isEmpty(misPayload.getSelectClause())) {
            queryBuilder = new StringBuilder("SELECT * FROM ").append(misPayload.getViewName());
        } else {
            queryBuilder = new StringBuilder("SELECT ");
            misPayload.getSelectClause().forEach(selectClause ->
                    queryBuilder.append(selectClause.getSelectName())
                            .append(",")
            );
            queryBuilder.deleteCharAt(queryBuilder.length() - 1);
            queryBuilder.append(" FROM ").append(misPayload.getViewName());
        }
        return queryBuilder;
    }

    private void prepareWhereClause(StringBuilder queryBuilder, MISPayload misPayload) {
        if (CollectionUtils.isEmpty(misPayload.getWhereClause()))
            return;

        queryBuilder.append(" WHERE ");
        misPayload.getWhereClause().forEach(mis -> {
            if (mis.getDefaultValue() instanceof List) {
                queryBuilder.append(mis.getWhere()).append(" IN (:").append(mis.getWhere()).append(") AND ");
            } else {
                queryBuilder.append(mis.getWhere()).append("=:").append(mis.getWhere()).append(" AND ");
            }
        });

        queryBuilder.setLength(queryBuilder.length() - 5);
    }

    private <T> void applyQueryParameters(Query<T> query, MISPayload misPayload) {
        if (CollectionUtils.isEmpty(misPayload.getWhereClause()))
            return;

        misPayload.getWhereClause().forEach(whereClause ->
                query.setParameter(whereClause.getWhere(), whereClause.getDefaultValue())
        );

    }


}
