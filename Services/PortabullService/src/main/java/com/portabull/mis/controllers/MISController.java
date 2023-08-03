package com.portabull.mis.controllers;

import com.portabull.constants.*;
import com.portabull.dbutils.dynamic.utils.DBUtils;
import com.portabull.execption.BadRequestException;
import com.portabull.mis.service.MISService;
import com.portabull.payloads.MISReportPayload;
import com.portabull.payloads.PageLimit;
import com.portabull.response.EmailResponse;
import com.portabull.response.FileResponse;
import com.portabull.response.PortableResponse;
import com.portabull.utils.DownloadUtils;
import com.portabull.utils.commonutils.CommonUtils;
import com.portabull.utils.validationutils.Validations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("MIS")
public class MISController {

    @Autowired
    DBUtils dbUtils;

    @Autowired
    MISService misService;

    Logger logger = LoggerFactory.getLogger(MISController.class);

    @GetMapping("/generate-reports")
    public ResponseEntity<?> generateReports(@RequestParam Long reportID, @RequestParam(required = false) String downloadFormat,
                                             @RequestParam(required = false) Integer startPage,
                                             @RequestParam(required = false) Integer endPage,
                                             @RequestParam(required = false) String pdfPassword,
                                             @RequestParam(required = false) boolean directDownload) {

        try {

            if (StringUtils.isEmpty(downloadFormat)) {
                downloadFormat = FileConstants.XLSX;
            }

            if (!FileConstants.XLSX.equalsIgnoreCase(downloadFormat) &&
                    !FileConstants.PDF.equalsIgnoreCase(downloadFormat) && !FileConstants.TEXT.equalsIgnoreCase(downloadFormat)
                    && !FileConstants.JSON.equalsIgnoreCase(downloadFormat)) {
                return new ResponseEntity<>(new FileResponse().
                        setStatus(PortableConstants.FAILED).
                        setMessage("Please specify the other valid format"), HttpStatus.BAD_REQUEST);
            }

            FileResponse response = misService.generateReport(reportID, downloadFormat, new PageLimit(startPage, endPage), pdfPassword);

            if (PortableConstants.FAILED.equalsIgnoreCase(response.getStatus())) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            misService.updateMisResponse(reportID, false, null);

            if (!directDownload) {
                return new ResponseEntity<>(misService.getBase64Response(response), HttpStatus.OK);
            }

            return DownloadUtils.download(
                    response.getFileName(), response.getMediaType(),
                    response.getInputStreamResource());
        } catch (Exception e) {
            logger.error(LoggerErrorConstants.EXCEPTION_OCCURRED, e);
            return new ResponseEntity<>(new FileResponse().
                    setMessage(MessageConstants.SERVICE_FAILED).
                    setStatus(PortableConstants.FAILED), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/sendMISToEMail")
    public ResponseEntity<PortableResponse> sendMISToEMail(@RequestBody Map<String, Object> data,
                                                           @RequestParam(required = false) Integer startPage,
                                                           @RequestParam(required = false) Integer endPage,
                                                           @RequestParam(required = false) String pdfPassword) {
        PortableResponse response = new PortableResponse();
        response.setStatus(PortableConstants.FAILED);
        response.setStatusCode(400L);

        try {
            String downloadFormat = "";

            if (CollectionUtils.isEmpty(data) || Validations.isEmptyObject(data.get(PortableConstants.EMAIL_PAYLOAD))) {
                response.setMessage("Please specify the mail id");
                return CommonUtils.returnResponse(response, HttpStatus.BAD_REQUEST);
            }

            if (Validations.isStringEmpty(data.get(MapKeyConstants.MIS_REPORT_ID))) {
                response.setMessage("Bad Request");
                return CommonUtils.returnResponse(response, HttpStatus.BAD_REQUEST);
            }

            if (Validations.isStringEmpty(data.get("downloadFormat")) ||
                    (!FileConstants.XLSX.equalsIgnoreCase(downloadFormat) &&
                            !FileConstants.PDF.equalsIgnoreCase(downloadFormat))) {
                downloadFormat = FileConstants.PDF;
            }

            FileResponse fileResponse = misService.generateReport(Long.valueOf(data.get(MapKeyConstants.MIS_REPORT_ID).toString()), downloadFormat,
                    new PageLimit(startPage, endPage), pdfPassword);

            EmailResponse emailResponse = misService.sendMISReportToEmail(fileResponse,
                    data.get(PortableConstants.EMAIL_PAYLOAD), Long.valueOf(data.get(MapKeyConstants.MIS_REPORT_ID).toString()));

            response.setMessage(emailResponse.getMessage());
            response.setStatus(emailResponse.getStatus());
            response.setStatusCode(emailResponse.getStatusCode());
        } catch (Exception e) {
            logger.error(LoggerErrorConstants.EXCEPTION_OCCURRED, e);
            response.setMessage(PortableConstants.FAILED);
            response.setStatus(PortableConstants.FAILED);
            response.setStatusCode(500L);
            return CommonUtils.returnResponse(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return CommonUtils.returnResponse(response, HttpStatus.OK);
    }

    @PostMapping("/get-mis-reports")
    public ResponseEntity<PortableResponse> getMISReports() {
        try {
            return CommonUtils.returnResponse(misService.getMISReports(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(LoggerErrorConstants.EXCEPTION_OCCURRED, e);
            PortableResponse response = new PortableResponse();
            response.setStatus(PortableConstants.FAILED);
            response.setStatusCode(500L);
            response.setMessage(MessageConstants.SERVICE_FAILED);
            return CommonUtils.returnResponse(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create-mis-report")
    public ResponseEntity<?> createMISReport(@RequestBody MISReportPayload payload) throws IOException {
        return new ResponseEntity<>(misService.createMISReports(payload), HttpStatus.OK);
    }

    @PostMapping("/get-columns")
    public ResponseEntity<?> getColumns(@RequestBody Map<String, String> payload) throws IOException {
        return new ResponseEntity<>(dbUtils.getViewDetails(payload.get("viewName")), HttpStatus.OK);
    }

    @PostMapping("/get-views")
    public ResponseEntity<?> getViews() throws IOException {
        return new ResponseEntity<>(dbUtils.getViews(), HttpStatus.OK);
    }

    @PostMapping("/query-it")
    public ResponseEntity<?> queryIt(@RequestBody Map<String, String> payload) throws SQLException {
        String query;
        List<Object> response = new ArrayList<>();

        try {

            try (Connection connection = DriverManager
                    .getConnection(payload.get("url"), payload.get("userName"), payload.get("pass"))) {

                if (payload.get("displayDBDesign") != null) {
                    query = "SELECT ROW_NUMBER() OVER (ORDER BY table_schema ASC) AS SrNo, table_name as TableName ,table_schema as schema , table_catalog ,table_type as TableType FROM information_schema.tables";
                } else {
                    query = payload.get("query");
                }

                if (query.trim().toUpperCase().startsWith("UPDATE") || query.trim().toUpperCase().startsWith("INSERT")) {
                    try (PreparedStatement statement = connection.prepareStatement(query)) {
                        statement.executeUpdate();
                        response.add(Arrays.asList("result"));
                        response.add(Arrays.asList(query.trim().toUpperCase().startsWith("UPDATE") ? "Updated Successfully" : "Inserted Successfully"));
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                }

                Integer limit = Integer.valueOf(payload.get("queryLimit"));

                if (query.contains("limit")) {
                    throw new BadRequestException("Not accepted limit in query already limit will apply 20 or 10000");
                }

                if (payload.get("displayDBDesign") == null) {

                    query = query.trim().endsWith(";") ? query.trim().substring(0, query.length() - 1) : query.trim();

                    int page = Integer.valueOf(payload.get("page"));

                    int offset = page * limit;

                    query = query + " limit " + limit + " OFFSET " + offset;

                }

                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    ResultSet rs = preparedStatement.executeQuery();
                    List<String> columnNames = new ArrayList<>();

                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                        String columnName = rs.getMetaData().getColumnName(i);
                        columnNames.add(columnName);
                    }

                    response.add(columnNames);
                    while (rs.next()) {
                        List data = new ArrayList();

                        for (String columnName : columnNames) {
                            data.add(rs.getObject(columnName));
                        }

                        response.add(data);
                    }


                }


            }
        } catch (Exception e) {
            return new ResponseEntity<>(new PortableResponse().
                    setMessage(e.getMessage()).
                    setStatus(PortableConstants.SUCCESS).setStatusCode(500l).setData("QUERY ERROR"), HttpStatus.OK);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
