package com.portabull.payloads.db;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.portabull.execption.BadRequestException;
import org.springframework.util.CollectionUtils;

import java.util.List;

@JsonIgnoreProperties
public class DatabaseEntity {

    private int pageNo;

    private int pageSize;

    private String entityName;

    private QueryBuilder queryBuilder;

    private List<DatabaseProperties> databaseProperties;

    public DatabaseEntity(String entityName, List<DatabaseProperties> databaseProperties) {
        this.entityName = entityName;
        this.databaseProperties = databaseProperties;
    }

    public String getEntityName() {
        return entityName;
    }

    public List<DatabaseProperties> getDatabaseProperties() {
        return databaseProperties;
    }

    public QueryBuilder buildPostgresQueryBuilder() {
        if (queryBuilder == null) {
            synchronized (this) {
                if (queryBuilder == null) {
                    queryBuilder = new PostgresQueryBuilder();
                }
            }
        }
        return queryBuilder;
    }

    public QueryBuilder buildMySQLQueryBuilder() {
        if (queryBuilder == null) {
            synchronized (this) {
                if (queryBuilder == null) {
                    queryBuilder = new MySQLQueryBuilder();
                }
            }
        }
        return queryBuilder;
    }

    public QueryBuilder buildOracleQueryBuilder() {
        if (queryBuilder == null) {
            synchronized (this) {
                if (queryBuilder == null) {
                    queryBuilder = new OracleQueryBuilder();
                }
            }
        }
        return queryBuilder;
    }

    public class MySQLQueryBuilder implements QueryBuilder {

        @Override
        public String generateCreateQuery() {
            return null;
        }

    }


    public class OracleQueryBuilder implements QueryBuilder {

        @Override
        public String generateCreateQuery() {
            return null;
        }

    }

    public class PostgresQueryBuilder implements QueryBuilder {
        @Override
        public String generateCreateQuery() {
            StringBuilder queryBuilder;

            validateEntityName();

            validateDatabaseProperties();

            queryBuilder = new StringBuilder("CREATE TABLE ").append(entityName).append("(id SERIAL PRIMARY KEY,");

            databaseProperties.forEach(databaseProperty -> {

                applyColumnNameWithDatatype(queryBuilder, databaseProperty);

                applyConstraints(queryBuilder, databaseProperty);

                queryBuilder.append(",");
            });

            queryBuilder.setLength(queryBuilder.length() - 1);

            queryBuilder.append(");");

            return queryBuilder.toString();
        }

        private void applyColumnNameWithDatatype(StringBuilder queryBuilder, DatabaseProperties databaseProperties) {

            validateColumnNameWithDataType(databaseProperties);

            queryBuilder.append(databaseProperties.getColumnName()).append(" ");

            switch (databaseProperties.getDataType()) {
                case "Number":
                    if (databaseProperties.getMinLength() >= -32768 || databaseProperties.getMaxLength() <= 32767) {
                        queryBuilder.append("smallint");
                    } else if (databaseProperties.getMinLength() >= -2147483648 || databaseProperties.getMaxLength() <= 2147483647) {
                        queryBuilder.append("integer");
                    } else if (databaseProperties.getMinLength() >= -9223372036854775808L || databaseProperties.getMaxLength() <= 9223372036854775807l) {
                        queryBuilder.append("bigint");
                    } else {
                        throw new BadRequestException("Minimum Length is -9223372036854775807l. Maximum length is 9223372036854775807l");
                    }
                    break;
                case "Decimal":
                    queryBuilder.append("decimal");
                    break;
                case "String":
                    if (databaseProperties.getMaxLength() < 255) {
                        queryBuilder.append("CHAR(").append(databaseProperties.getMaxLength()).append(")");
                    } else if (databaseProperties.getMaxLength() < 65535) {
                        queryBuilder.append("VARCHAR(").append(databaseProperties.getMaxLength()).append(")");
                    } else {
                        throw new BadRequestException("Invalid Length");
                    }
                    break;
                case "TEXT":
                    queryBuilder.append("TEXT");
                    break;
                case "BOOLEAN":
                    queryBuilder.append("BOOLEAN");
                    break;
                default:
                    throw new BadRequestException("Invalid Datatype");
            }
        }

        private void validateColumnNameWithDataType(DatabaseProperties databaseProperties) {
            if (databaseProperties.getColumnName() == null || databaseProperties.getColumnName().length() == 0) {
                throw new BadRequestException("PropertyName should not be empty");
            }

            if (databaseProperties.getColumnName().length() > 30) {
                throw new BadRequestException("PropertyName should not be greater than 30 characters");
            }
        }


        private void applyConstraints(StringBuilder queryBuilder, DatabaseProperties databaseProperties) {
            if (databaseProperties.isUniqueValue()) {
                queryBuilder.append(" unique ");
            }

            if (databaseProperties.isMandatory()) {
                queryBuilder.append(" NOT NULL ");
            }
        }


        private void validateDatabaseProperties() {
            if (CollectionUtils.isEmpty(databaseProperties))
                throw new BadRequestException("");
        }

        private void validateEntityName() {
            if (entityName == null || entityName.length() == 0) {
                throw new BadRequestException("EntityName should not be empty");
            }

            if (entityName.length() > 30) {
                throw new BadRequestException("EntityName should not be greater than 30 characters");
            }
        }
    }

    public interface QueryBuilder {

        public String generateCreateQuery();

    }

}
