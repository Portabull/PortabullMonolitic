package com.portabull.audit.models;

import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "audit_details", schema = DatabaseSchema.PORTABULL_GENERIC, catalog = DatabaseSchema.PORTABULL_GENERIC)
public class AuditDetails implements Serializable {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_audit")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    private Long id;

    @Column(name = "data",length = 5000)
    private String data;

    @Column(name = "entity_name")
    private String entityName;

    @Column(name = "operation_type")
    private String operationType;

    public Long getId() {
        return id;
    }

    public AuditDetails setId(Long id) {
        this.id = id;
        return this;
    }

    public String getData() {
        return data;
    }

    public AuditDetails setData(String data) {
        this.data = data;
        return this;
    }

    public String getEntityName() {
        return entityName;
    }

    public AuditDetails setEntityName(String entityName) {
        this.entityName = entityName;
        return this;
    }

    public String getOperationType() {
        return operationType;
    }

    public AuditDetails setOperationType(String operationType) {
        this.operationType = operationType;
        return this;
    }
}
