package com.portabull.spf.models;

import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "spf_form_manager", schema = DatabaseSchema.SPF, catalog = DatabaseSchema.SPF)
public class SPFFormManager {

    @Id
    @Column(name = "form_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_spf_form_manager_id")
    private Long formID;

    @Column(name = "form_name", nullable = false)
    private String formName;

    @Column(name = "form_label", nullable = false)
    private String formLabel;

    @Column(name = "form_description")
    private String formDescription;

    @Column(name = "form_parent_table")
    private String formParentTable;

    @Column(name = "form_screning_data")
    private String formScreningData;

    @Column(name = "form_mapped_to_all_users")
    private Boolean formMappedToAllUsers;

    @OneToMany(mappedBy = "spfFormManager", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<SPFFormChildTables> spfFormChildTables;

    public Long getFormID() {
        return formID;
    }

    public void setFormID(Long formID) {
        this.formID = formID;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getFormLabel() {
        return formLabel;
    }

    public void setFormLabel(String formLabel) {
        this.formLabel = formLabel;
    }

    public String getFormDescription() {
        return formDescription;
    }

    public void setFormDescription(String formDescription) {
        this.formDescription = formDescription;
    }

    public String getFormParentTable() {
        return formParentTable;
    }

    public void setFormParentTable(String formParentTable) {
        this.formParentTable = formParentTable;
    }

    public String getFormScreningData() {
        return formScreningData;
    }

    public void setFormScreningData(String formScreningData) {
        this.formScreningData = formScreningData;
    }

    public Boolean getFormMappedToAllUsers() {
        return formMappedToAllUsers;
    }

    public void setFormMappedToAllUsers(Boolean formMappedToAllUsers) {
        this.formMappedToAllUsers = formMappedToAllUsers;
    }

    public List<SPFFormChildTables> getSpfFormChildTables() {
        return spfFormChildTables;
    }

    public void setSpfFormChildTables(List<SPFFormChildTables> spfFormChildTables) {
        this.spfFormChildTables = spfFormChildTables;
    }

}
