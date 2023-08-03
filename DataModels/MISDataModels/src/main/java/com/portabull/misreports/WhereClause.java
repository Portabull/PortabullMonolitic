package com.portabull.misreports;

import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "mis_where_clause", schema = DatabaseSchema.MIS, catalog = DatabaseSchema.MIS)
public class WhereClause implements Serializable {

    @Id
    @Column(name = "where_id")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_mis_where")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    private Long whereID;

    @Column(name = "mis_id")
    private Long misID;

    @Column(name = "user_id", nullable = false)
    private Long userID;

    @Column(name = "where_clause", nullable = false)
    private String whereCondition;

    @Column(name = "default_value")
    private String defaultValue;

    public Long getWhereID() {
        return whereID;
    }

    public void setWhereID(Long whereID) {
        this.whereID = whereID;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getWhereCondition() {
        return whereCondition;
    }

    public void setWhereCondition(String whereCondition) {
        this.whereCondition = whereCondition;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Long getMisID() {
        return misID;
    }

    public void setMisID(Long misID) {
        this.misID = misID;
    }
}
