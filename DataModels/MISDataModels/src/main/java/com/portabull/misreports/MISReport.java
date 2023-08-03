package com.portabull.misreports;

import com.portabull.constants.DatabaseSchema;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "mis_report", schema = DatabaseSchema.MIS, catalog = DatabaseSchema.MIS)
public class MISReport implements Serializable {

    @Id
    @Column(name = "mis_id")
    @SequenceGenerator(name = "seq", sequenceName = ".seq_mis_report")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    private Long misID;

    @Column(name = "user_id", nullable = false)
    private Long userID;

    @Column(name = "mis_name", nullable = false)
    private String misName;

    @Column(name = "view_name", nullable = false)
    private String viewName;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "misID")
    private Set<WhereClause> whereClause;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "misID")
    private Set<SelectClause> selectClause;

    @Column(name = "group_by")
    private String groupBy;

    @Column(name = "order_by")
    private String orderBy;

    @Column(name = "timestamp_required")
    private boolean isTimeStampRequired;

    @Column(name = "timestamp_format")
    private String timeStampFormat;

    @Column(name = "document_tittle")
    private String documentTittle;

    @Column(name = "logo_dms_id")
    private Long logoDMSId;

    @Column(name = "temp_logo_image_path_for_pdf")
    private String tempLogoImagePath;

    @Column(name = "is_logo_required")
    private boolean isLogoRequired;

    @Column(name = "header_color_code")
    private String headerColorCode;

    @Column(name = "download_count")
    private Long downloadCount;

    @Column(name = "mail_count")
    private Long mailCount;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "misID")
    private Set<MISMailAudit> misMailAudits;

    public Long getMisID() {
        return misID;
    }

    public void setMisID(Long misID) {
        this.misID = misID;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getMisName() {
        return misName;
    }

    public void setMisName(String misName) {
        this.misName = misName;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public List<WhereClause> getWhereClause() {
        if (CollectionUtils.isEmpty(whereClause))
            return Collections.emptyList();
        return new ArrayList<>(whereClause);
    }

    public void setWhereClause(Set<WhereClause> whereClause) {
        this.whereClause = whereClause;
    }

    public void setSelectClause(Set<SelectClause> selectClause) {
        this.selectClause = selectClause;
    }

    public List<SelectClause> getSelectClause() {
        if (CollectionUtils.isEmpty(selectClause))
            return Collections.emptyList();
        return selectClause.stream().sorted(Comparator.comparing(SelectClause::getSelectId)).collect(Collectors.toList());
    }


    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public boolean isTimeStampRequired() {
        return isTimeStampRequired;
    }

    public void setTimeStampRequired(boolean timeStampRequired) {
        isTimeStampRequired = timeStampRequired;
    }

    public String getTimeStampFormat() {
        return timeStampFormat;
    }

    public void setTimeStampFormat(String timeStampFormat) {
        this.timeStampFormat = timeStampFormat;
    }

    public String getDocumentTittle() {
        return documentTittle;
    }

    public void setDocumentTittle(String documentTittle) {
        this.documentTittle = documentTittle;
    }

    public String getTempLogoImagePath() {
        return tempLogoImagePath;
    }

    public void setTempLogoImagePath(String tempLogoImagePath) {
        this.tempLogoImagePath = tempLogoImagePath;
    }

    public boolean isLogoRequired() {
        return isLogoRequired;
    }

    public void setLogoRequired(boolean logoRequired) {
        isLogoRequired = logoRequired;
    }

    public String getHeaderColorCode() {
        return headerColorCode;
    }

    public void setHeaderColorCode(String headerColorCode) {
        this.headerColorCode = headerColorCode;
    }

    public Long getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Long downloadCount) {
        this.downloadCount = downloadCount;
    }

    public Long getMailCount() {
        return mailCount;
    }

    public void setMailCount(Long mailCount) {
        this.mailCount = mailCount;
    }

    public List<MISMailAudit> getMisMailAudits() {
        if (CollectionUtils.isEmpty(misMailAudits))
            return Collections.emptyList();
        return misMailAudits.stream().sorted(Comparator.comparing(MISMailAudit::getMailID)).collect(Collectors.toList());
    }

    public void setMisMailAudits(Set<MISMailAudit> misMailAudits) {
        this.misMailAudits = misMailAudits;
    }

    public Long getLogoDMSId() {
        return logoDMSId;
    }

    public void setLogoDMSId(Long logoDMSId) {
        this.logoDMSId = logoDMSId;
    }
}
