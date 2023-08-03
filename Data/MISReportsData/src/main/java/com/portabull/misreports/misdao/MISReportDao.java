package com.portabull.misreports.misdao;

import com.portabull.misreports.MISMailAudit;
import com.portabull.misreports.MISReport;
import com.portabull.payloads.MISPayload;
import com.portabull.payloads.MISReportPayload;
import com.portabull.payloads.PageLimit;
import com.portabull.response.PortableResponse;

import java.util.List;

public interface MISReportDao {

    public MISReport getMISReport(Long reportID);

    public <T> T getReport(MISPayload payload, String format, PageLimit pageLimit);

    public MISMailAudit saveMisMailAudit(MISMailAudit misMailAudit);

    public MISReport saveMisReport(MISReport misReport);

    public List<MISReport> getMISReportsbyUserID(Long userID);

    PortableResponse createMISReport(MISReportPayload payload, String tempLogoImagePath, Long logoDMSId);

    void validateQuery(MISPayload payload);
}
