package com.portabull.mis.service.utils;

import com.portabull.constants.FileConstants;
import com.portabull.constants.PortableConstants;
import com.portabull.misreports.misdao.MISReportDao;
import com.portabull.payloads.EmailPayload;
import com.portabull.payloads.MISPayload;
import com.portabull.payloads.PageLimit;
import com.portabull.response.EmailResponse;
import com.portabull.response.FileResponse;
import com.portabull.utils.fileutils.FileHandling;
import com.portabull.utils.validationutils.Validations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class MISHelperUtils {

    @Autowired
    MISReportDao misDao;

    public EmailResponse prepareEmailPayload(EmailPayload payload, Map<String, Object> emailPayload) {
        EmailResponse response = new EmailResponse();
        response.setHasErrors(false);
        if (Validations.isEmptyObject(emailPayload.get("to"))) {
            response.setHasErrors(true);
            response.setStatus(PortableConstants.FAILED);
            response.setMessage("Please specify the any to address");
            response.setStatusCode(400L);
            return response;
        }

        if (emailPayload.get("to") instanceof List) {
            payload.setTo((List) emailPayload.get("to"));
        } else {
            payload.setTo(Arrays.asList(emailPayload.get("to").toString()));
        }

        if (!Validations.isEmptyObject(emailPayload.get("cc"))) {
            if (emailPayload.get("cc") instanceof List) {
                payload.setCc((List) emailPayload.get("cc"));
            } else {
                payload.setCc(Arrays.asList(emailPayload.get("cc").toString()));
            }
        }

        if (!Validations.isEmptyObject(emailPayload.get("bcc"))) {
            if (emailPayload.get("bcc") instanceof List) {
                payload.setBcc((List) emailPayload.get("bcc"));
            } else {
                payload.setBcc(Arrays.asList(emailPayload.get("bcc").toString()));
            }
        }

        return response;
    }


    public FileResponse generateMISReport(MISPayload payload, String downloadFormat, PageLimit pageLimit, String pdfPassword) throws IOException {
        switch (downloadFormat) {
            case FileConstants.PDF:
                return FileHandling.generateReportAsPdf(misDao.getReport(payload, downloadFormat, pageLimit), payload, pdfPassword);
            case FileConstants.TEXT:
                return FileHandling.generateReportAsText(misDao.getReport(payload, downloadFormat, pageLimit), payload);
            case FileConstants.JSON:
                return FileHandling.generateReportAsJSON(misDao.getReport(payload, downloadFormat, pageLimit));
            default:
                return FileHandling.generateReportAsExcel(misDao.getReport(payload, downloadFormat, pageLimit), payload);
        }
    }
}
