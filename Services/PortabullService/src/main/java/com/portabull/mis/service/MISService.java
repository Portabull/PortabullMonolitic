package com.portabull.mis.service;

import com.portabull.payloads.EmailPayload;
import com.portabull.payloads.MISReportPayload;
import com.portabull.payloads.PageLimit;
import com.portabull.response.EmailResponse;
import com.portabull.response.FileResponse;
import com.portabull.response.PortableResponse;

import java.io.IOException;

public interface MISService {

    public FileResponse generateReport(Long reportID, String downloadFormat, PageLimit pageLimit, String pdfPassword) throws IOException;

    public EmailResponse sendMISReportToEmail(FileResponse fileResponse, Object emailPayload, Long reportID);

    public void updateMisResponse(Long reportID, boolean isMISMail, EmailPayload emailPayload);

    public PortableResponse getMISReports();

    PortableResponse getBase64Response(FileResponse response) throws IOException;

    PortableResponse createMISReports(MISReportPayload payload) throws IOException;
}
