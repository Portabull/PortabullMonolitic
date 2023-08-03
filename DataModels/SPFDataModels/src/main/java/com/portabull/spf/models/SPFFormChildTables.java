package com.portabull.spf.models;

import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;

@Entity
@Table(name = "spf_form_child_tables", schema = DatabaseSchema.SPF, catalog = DatabaseSchema.SPF)
public class SPFFormChildTables {

    @Id
    @Column(name = "form_child_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_spf_form_child_id")
    private Long formChildId;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "form_id")
    private SPFFormManager spfFormManager;

    @Column(name = "child_table_name", nullable = false)
    private String childTableName;

    public Long getFormChildId() {
        return formChildId;
    }

    public void setFormChildId(Long formChildId) {
        this.formChildId = formChildId;
    }

    public SPFFormManager getSpfFormManager() {
        return spfFormManager;
    }

    public void setSpfFormManager(SPFFormManager spfFormManager) {
        this.spfFormManager = spfFormManager;
    }

    public String getChildTableName() {
        return childTableName;
    }

    public void setChildTableName(String childTableName) {
        this.childTableName = childTableName;
    }
}
