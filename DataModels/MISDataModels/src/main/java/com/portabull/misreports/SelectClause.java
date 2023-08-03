package com.portabull.misreports;

import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "mis_select_clause", schema = DatabaseSchema.MIS, catalog = DatabaseSchema.MIS)
public class SelectClause implements Serializable {

    @Id
    @Column(name = "select_id")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_select_clause")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    private Long selectId;

    @Column(name = "mis_id")
    private Long misID;

    @Column(name = "select_name", nullable = false)
    private String selectName;

    @Column(name = "alias_name")
    private String aliasName;

    public Long getSelectId() {
        return selectId;
    }

    public void setSelectId(Long selectId) {
        this.selectId = selectId;
    }

    public String getSelectName() {
        return selectName;
    }

    public void setSelectName(String selectName) {
        this.selectName = selectName;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public Long getMisID() {
        return misID;
    }

    public void setMisID(Long misID) {
        this.misID = misID;
    }
}
