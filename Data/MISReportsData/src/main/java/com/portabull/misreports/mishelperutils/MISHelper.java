package com.portabull.misreports.mishelperutils;

import com.portabull.misreports.MISMailAudit;
import com.portabull.misreports.MISReport;
import com.portabull.misreports.SelectClause;
import com.portabull.misreports.WhereClause;
import com.portabull.payloads.MISMailAuditPayload;
import com.portabull.payloads.MISPayload;
import com.portabull.payloads.SelectPayload;
import com.portabull.payloads.WherePayload;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Payload to Entity and Entity to Payload Converter
 */

public class MISHelper {

    private MISHelper() {

    }

    public static MISPayload convertMISReportTOMISPayload(MISReport report) {
        MISPayload payload = new MISPayload();
        payload.setMisID(report.getMisID());
        payload.setUserID(report.getUserID());
        payload.setMisName(report.getMisName());
        payload.setViewName(report.getViewName());
        payload.setGroupBy(report.getGroupBy());
        payload.setOrderBy(report.getOrderBy());
        payload.setTimeStampRequired(report.isTimeStampRequired());
        payload.setTimeStampFormat(report.getTimeStampFormat());
        payload.setDocumentTittle(report.getDocumentTittle());
        payload.setTempLogoImagePath(report.getTempLogoImagePath());
        payload.setDmsDocumentId(report.getLogoDMSId());
        payload.setLogoRequired(report.isLogoRequired());
        payload.setHeaderColorCode(report.getHeaderColorCode());
        payload.setDownloadCount(report.getDownloadCount());
        payload.setMailCount(report.getMailCount());
        payload.setSelectClause(convertSelectClauseTOSelectPayload(report.getSelectClause()));
        payload.setWhereClause(convertWhereClauseTOWherePayload(report.getWhereClause()));
        payload.setMisMailAudits(convertMisMailAuditTOMisMailAuditPayload(report.getMisMailAudits()));
        return payload;
    }

    private static List<MISMailAuditPayload> convertMisMailAuditTOMisMailAuditPayload(List<MISMailAudit> misMailAudits) {
        List<MISMailAuditPayload> auditPayloads = new ArrayList<>();
        if (CollectionUtils.isEmpty(auditPayloads)) {
            return auditPayloads;
        }
        misMailAudits.forEach(misMailAudit -> {
            MISMailAuditPayload misMailAuditPayload = new MISMailAuditPayload();
            misMailAuditPayload.setMailID(misMailAudit.getMailID());
            misMailAuditPayload.setMisMailData(misMailAudit.getMisMailData());
            misMailAuditPayload.setMisID(misMailAudit.getMisID());
            auditPayloads.add(misMailAuditPayload);
        });
        return auditPayloads;
    }

    public static List<SelectPayload> convertSelectClauseTOSelectPayload(List<SelectClause> selectClauses) {
        List<SelectPayload> selectPayloads = new ArrayList<>();
        if (CollectionUtils.isEmpty(selectClauses)) {
            return selectPayloads;
        }
        selectClauses.forEach(selectClause -> {
            SelectPayload payload = new SelectPayload();
            payload.setSelectId(selectClause.getSelectId());
            payload.setSelectName(selectClause.getSelectName());
            payload.setAliasName(selectClause.getAliasName());
            payload.setMisID(selectClause.getMisID());
            selectPayloads.add(payload);
        });
        return selectPayloads;
    }

    public static List<WherePayload> convertWhereClauseTOWherePayload(List<WhereClause> whereClauses) {
        List<WherePayload> wherePayloads = new ArrayList<>();
        if (CollectionUtils.isEmpty(whereClauses)) {
            return wherePayloads;
        }
        whereClauses.forEach(whereClause -> {
            WherePayload payload = new WherePayload();
            payload.setWhereID(whereClause.getWhereID());
            payload.setWhere(whereClause.getWhereCondition());
            payload.setDefaultValue(whereClause.getDefaultValue());
            payload.setUserID(whereClause.getUserID());
            payload.setMisID(whereClause.getMisID());
            wherePayloads.add(payload);
        });
        return wherePayloads;
    }

}
