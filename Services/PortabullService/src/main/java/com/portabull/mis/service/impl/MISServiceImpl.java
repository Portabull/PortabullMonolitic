package com.portabull.mis.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portabull.constants.MessageConstants;
import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.dms.service.DocumentService;
import com.portabull.execption.BadRequestException;
import com.portabull.mis.service.MISService;
import com.portabull.mis.service.utils.MISHelperUtils;
import com.portabull.misreports.MISMailAudit;
import com.portabull.misreports.MISReport;
import com.portabull.misreports.misdao.MISReportDao;
import com.portabull.misreports.mishelperutils.MISHelper;
import com.portabull.payloads.EmailPayload;
import com.portabull.payloads.MISPayload;
import com.portabull.payloads.MISReportPayload;
import com.portabull.payloads.PageLimit;
import com.portabull.response.DocumentResponse;
import com.portabull.response.EmailResponse;
import com.portabull.response.FileResponse;
import com.portabull.response.PortableResponse;
import com.portabull.utils.HomePageUrl;
import com.portabull.utils.MockMultipartFile;
import com.portabull.utils.commonutils.CommonUtils;
import com.portabull.utils.dateutils.DateUtils;
import com.portabull.utils.emailutils.EmailUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.activation.MimetypesFileTypeMap;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class MISServiceImpl implements MISService {

    @Autowired
    MISReportDao misDao;

    @Autowired
    EmailUtils emailUtils;

    @Autowired
    MISHelperUtils misHelperUtils;

    @Autowired
    HomePageUrl homePageUrl;

    @Autowired
    DocumentService documentService;

    @Value("${mis.temp.storage}")
    String misTempStorage;

    static final Logger logger = LoggerFactory.getLogger(MISServiceImpl.class);

    @Override
    public FileResponse generateReport(Long reportID, String downloadFormat, PageLimit pageLimit, String pdfPassword) throws IOException {

        MISReport misReport = misDao.getMISReport(reportID);

        if (misReport == null)
            return new FileResponse().
                    setMessage("Please select the valid Report ID(Report Not Found)").
                    setStatus(PortableConstants.FAILED);

        MISPayload payload = MISHelper.convertMISReportTOMISPayload(misReport);

        try {
            downloadLogoToTempStorage(payload);
        } catch (Exception e) {
            throw new IOException(e);
        }

        return misHelperUtils.generateMISReport(payload, downloadFormat, pageLimit, pdfPassword);

    }

    @Override
    public EmailResponse sendMISReportToEmail(FileResponse fileResponse, Object emailPayload, Long reportID) {

        EmailPayload payload = new EmailPayload();

        if (emailPayload instanceof Map) {
            EmailResponse emailResponse = misHelperUtils.prepareEmailPayload(payload, (Map<String, Object>) emailPayload);
            if (emailResponse.hasErrors()) {
                return emailResponse;
            }
        } else if (emailPayload instanceof EmailPayload) {
            payload = (EmailPayload) emailPayload;
        } else {
            throw new IllegalArgumentException();
        }

        payload.setAttachment(fileResponse.getFile());
        payload.setSubject("MIS Reports");
        payload.setMessage("Hi Please find the attachment of MIS");
        payload.setBody("HI");
        payload.setHtmlTemplete(false);

        EmailResponse mailResponse = emailUtils.sendEmail(payload);
        if (!mailResponse.hasErrors()) {
            updateMisResponse(reportID, true, payload);
        }
        return mailResponse;
    }

    @Override
    public void updateMisResponse(Long reportID, boolean isMISMail, EmailPayload emailPayload) {
        try {
            MISReport misReport = misDao.getMISReport(reportID);
            if (misReport == null)
                return;

            misReport.setDownloadCount(misReport.getDownloadCount() != null ? misReport.getDownloadCount() + 1 : 1L);

            if (isMISMail) {
                misReport.setMailCount(misReport.getMailCount() != null ? misReport.getMailCount() + 1 : 1L);
                MISMailAudit misMailAudit = new MISMailAudit();
                misMailAudit.setMisID(misReport.getMisID());
                misMailAudit.setMisMailData(new ObjectMapper().writeValueAsString(emailPayload));
                misDao.saveMisMailAudit(misMailAudit);
            }

            misDao.saveMisReport(misReport);
        } catch (Exception e) {
            logger.error("While updating the mis response it throws error", e);
        }
    }


    @Override
    public PortableResponse getMISReports() {
        Long userID = CommonUtils.getLoggedInUserId();
        List<MISReport> misReports = misDao.getMISReportsbyUserID(userID);
        if (CollectionUtils.isEmpty(misReports)) {
            return new PortableResponse(
                    MessageConstants.EMPTY_MIS_REPORTS,
                    200L, PortableConstants.SUCCESS, null);
        }

        List<Map<String, Object>> response = new ArrayList<>();
        AtomicReference<Integer> sno = new AtomicReference<>(1);
        misReports.forEach(report -> {
            Map<String, Object> chunk = new LinkedHashMap<>();
            chunk.put("sno", sno.getAndSet(sno.get() + 1));
            chunk.put("reportID", report.getMisID());
            chunk.put("reportName", report.getMisName());
            chunk.put("documentTittle", report.getDocumentTittle());
            response.add(chunk);
        });
        return new PortableResponse(PortableConstants.SUCCESS, 200L, PortableConstants.SUCCESS, response);
    }

    @Override
    public PortableResponse getBase64Response(FileResponse response) throws IOException {

        Map<String, Object> fileResponse = new HashMap<>();

        fileResponse.put("file", "data:;base64," + Base64.getEncoder().encodeToString(IOUtils.toByteArray(response.getInputStream())));

        fileResponse.put("fileName", response.getFileName());

        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, fileResponse);

    }

    @Override
    public PortableResponse createMISReports(MISReportPayload payload) throws IOException {

        Long documentId = null;

        String tempFilePath = null;

        validatePayload(payload);

        if (!StringUtils.isEmpty(payload.getFile())) {

            documentId = uploadFileToDMS(payload);

            tempFilePath = writeFileToMISTempStorage(payload.getFile().split(",")[1], payload.getFileName(), documentId);
        }

        return misDao.createMISReport(payload, tempFilePath, documentId);
    }

    private void validatePayload(MISReportPayload payload) {
        try {

            if (!StringUtils.isEmpty(payload.getTimeStampFormat())) {
                DateUtils.getTimeAsString(payload.getTimeStampFormat());
            }

            if (!StringUtils.isEmpty(payload.getHeaderColourCode())) {
                Color.decode(payload.getHeaderColourCode());
            }

            MISPayload misPayload = new MISPayload();

            misPayload.setViewName(payload.getViewName());

            misPayload.setOrderBy(payload.getOrderBy());

            misPayload.setGroupBy(payload.getGroupBy());

            misPayload.setUserName(payload.getUserName());

            misDao.validateQuery(misPayload);

            payload.setUserId(misPayload.getUserID());

        } catch (NumberFormatException num) {
            throw new BadRequestException("Invalid HeaderColour Code");
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid Timestamp format");
        } catch (InvalidDataAccessResourceUsageException e) {
            throw new BadRequestException("Invalid GroupBy/OrderBy, PLease check and update");
        }
    }

    private Long uploadFileToDMS(MISReportPayload payload) throws IOException {

        byte[] fileBytes = Base64.getDecoder().decode(payload.getFile().split(",")[1]);

        MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();

        DocumentResponse documentResponse;
        try {
            documentResponse = documentService.uploadDocument(new MockMultipartFile(payload.getFileName(),
                    payload.getFileName(), fileTypeMap.getContentType(payload.getFileName()),
                    fileBytes.length, fileBytes, null));
        } catch (Exception e) {
            throw new IOException(e);
        }

        return documentResponse.getDocumentID();
    }

    private void downloadLogoToTempStorage(MISPayload payload) throws Exception {

        if (!payload.isLogoRequired())
            return;


        File tempFile = new File(payload.getTempLogoImagePath());

        if (!tempFile.exists()) {

            try {

                DocumentResponse documentResponse = documentService.downloadDocument(payload.getDmsDocumentId());

                Map<String, Object> fileResponse = new HashMap<>();

                String contentType = "";
                if (documentResponse.getFileResponse().getFileName().endsWith(".pdf")) {
                    contentType = MediaType.APPLICATION_PDF_VALUE;
                }

                fileResponse.put("file", "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(IOUtils.toByteArray(documentResponse.getFileResponse().getInputStream())));

                fileResponse.put("fileName", documentResponse.getFileResponse().getFileName());

                payload.setTempLogoImagePath(writeFileToMISTempStorage(fileResponse.get("file").toString().split(",")[1],
                        fileResponse.get("fileName").toString(), payload.getDmsDocumentId()));

            } catch (IOException e) {
                logger.error("Exception Occurred", e);
            }
        }
    }

    public String writeFileToMISTempStorage(String file, String fileName, Long dmsDocumentId) throws IOException {

        String dirPath = misTempStorage + File.separator + "mis_doc_" + dmsDocumentId;

        String absoluteFilePath = dirPath + File.separator + fileName;

        File dir = new File(dirPath);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        byte[] decodedBytes = Base64.getDecoder().decode(file);

        Files.write(Paths.get(absoluteFilePath), decodedBytes);

        return absoluteFilePath;
    }


}
