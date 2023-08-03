package com.portabull.spf.models;


import com.portabull.constants.DatabaseSchema;
import com.portabull.um.UserCredentials;
import com.portabull.um.UserProfile;

import javax.persistence.*;

@Entity
@Table(name = "spf_user_form_mapping", schema = DatabaseSchema.SPF, catalog = DatabaseSchema.SPF,
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"form_id", "user_id"}))
public class SPFUserFormMapping {

    @Id
    @Column(name = "user_form_mapping_id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_spf_user_form_mapping_id")
    private Long userFormMappingId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "form_id")
    private SPFFormManager spfFormManager;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private UserCredentials userCredentials;

    public Long getUserFormMappingId() {
        return userFormMappingId;
    }

    public void setUserFormMappingId(Long userFormMappingId) {
        this.userFormMappingId = userFormMappingId;
    }

    public SPFFormManager getSpfFormManager() {
        return spfFormManager;
    }

    public void setSpfFormManager(SPFFormManager spfFormManager) {
        this.spfFormManager = spfFormManager;
    }

    public UserCredentials getUserCredentials() {
        return userCredentials;
    }

    public void setUserCredentials(UserCredentials userCredentials) {
        this.userCredentials = userCredentials;
    }

}
