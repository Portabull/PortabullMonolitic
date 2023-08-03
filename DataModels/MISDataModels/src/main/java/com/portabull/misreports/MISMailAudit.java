package com.portabull.misreports;

import com.portabull.constants.DatabaseSchema;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "mis_mail_audit", schema = DatabaseSchema.MIS, catalog = DatabaseSchema.MIS)
public class MISMailAudit implements Serializable {

    @Id
    @Column(name = "mail_id")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_mis_mail")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    private Long mailID;

    @Column(name = "mis_id")
    private Long misID;

    @Column(name = "mis_mail_data")
    private String misMailData;

    public Long getMailID() {
        return mailID;
    }

    public void setMailID(Long mailID) {
        this.mailID = mailID;
    }

    public Long getMisID() {
        return misID;
    }

    public void setMisID(Long misID) {
        this.misID = misID;
    }

    public String getMisMailData() {
        return misMailData;
    }

    public void setMisMailData(String misMailData) {
        this.misMailData = misMailData;
    }
}
