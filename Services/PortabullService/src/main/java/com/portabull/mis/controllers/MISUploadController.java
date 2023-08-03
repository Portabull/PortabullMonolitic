package com.portabull.mis.controllers;

import com.portabull.constants.FileConstants;
import com.portabull.constants.MessageConstants;
import com.portabull.constants.PortableConstants;
import com.portabull.mis.service.MISService;
import com.portabull.mis.service.MISUploadService;
import com.portabull.payloads.PageLimit;
import com.portabull.response.FileResponse;
import com.portabull.response.PortableResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("MIS")
public class MISUploadController {

    static final Logger logger = LoggerFactory.getLogger(MISUploadController.class);

    @Autowired
    MISService misService;

    @Autowired
    MISUploadService misUploadService;

    @GetMapping("/uploadMISReport")
    public ResponseEntity<PortableResponse> generateReports(@RequestParam Long reportID, @RequestParam(required = false) String fileFormat,
                                                            @RequestParam(required = false) Integer startPage,
                                                            @RequestParam(required = false) Integer endPage,
                                                            @RequestParam(required = false) String pdfPassword) {

        try {

            if (StringUtils.isEmpty(fileFormat)) {
                fileFormat = FileConstants.XLSX;
            }

            if (!FileConstants.XLSX.equalsIgnoreCase(fileFormat) &&
                    !FileConstants.PDF.equalsIgnoreCase(fileFormat) && !FileConstants.TEXT.equalsIgnoreCase(fileFormat)) {
                return new ResponseEntity<>(new PortableResponse().
                        setStatus(PortableConstants.FAILED).
                        setMessage("Please specify the valid file format"), HttpStatus.BAD_REQUEST);
            }

            FileResponse response = misService.generateReport(reportID, fileFormat, new PageLimit(startPage, endPage), pdfPassword);

            if (PortableConstants.FAILED.equalsIgnoreCase(response.getStatus())) {
                return new ResponseEntity<>(new PortableResponse().
                        setStatus(response.getStatus()).
                        setMessage(response.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity<>(misUploadService.uploadFileToDMS(response, fileFormat), HttpStatus.OK);

        } catch (Exception e) {
            logger.error("while generating reports failed ", e);
            return new ResponseEntity<>(new PortableResponse().
                    setMessage(MessageConstants.SERVICE_FAILED).
                    setStatus(PortableConstants.FAILED), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
