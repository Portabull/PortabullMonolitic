package com.portabull.genericservice.controllers;

import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.execption.BadRequestException;
import com.portabull.genericservice.models.CompanyDetails;
import com.portabull.genericservice.service.AdService;
import com.portabull.response.PortableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("ad")
public class AdController {

    @Autowired
    AdService adService;

    @PostMapping("save-company-info")
    public ResponseEntity<PortableResponse> saveCompanyInfo(@RequestBody CompanyDetails companyDetails) {

        return new ResponseEntity<>(adService.saveCompanyDetails(companyDetails), HttpStatus.OK);

    }

    @PostMapping("get-company-info")
    public ResponseEntity<PortableResponse> getCompanyInfo(@RequestBody Map<String, Object> payload) {

        if (payload.get("pageNo") == null) {
            return new ResponseEntity<>(new PortableResponse("Page Number is mandatory",
                    StatusCodes.C_400, PortableConstants.FAILED, null), HttpStatus.BAD_REQUEST);
        }

        Long pageNo = Long.valueOf(payload.get("pageNo").toString());

        Long resultSize = StringUtils.isEmpty(payload.get("resultSize")) ? 10 : Long.valueOf(payload.get("resultSize").toString());

        pageNo = pageNo * resultSize;

        return new ResponseEntity<>(adService.getCompanyDetails(pageNo, resultSize), HttpStatus.OK);

    }

    @PostMapping("/search-companies")
    public ResponseEntity<?> searchCompanies(@RequestBody Map<String, Object> payload) {

        if (StringUtils.isEmpty(payload.get("search"))) {
            throw new BadRequestException("Search is Empty");
        }

        return new ResponseEntity<>(adService.searchCompanies(payload.get("search").toString()), HttpStatus.OK);
    }

    @PostMapping("/delete-companies")
    public ResponseEntity<?> deleteCompanies(@RequestBody Map<String, Object> payload) {

        if (StringUtils.isEmpty(payload.get("companies"))) {
            throw new BadRequestException("companies is Empty");
        }

        List<Long> companies = (List<Long>) payload.get("companies");

        return new ResponseEntity<>(adService.deleteCompanies(companies), HttpStatus.OK);
    }

    @GetMapping("get-company-info")
    public ResponseEntity<?> getCompanyInfo(Long clientId) {

        return new ResponseEntity<>(adService.getCompanyDetails(clientId), HttpStatus.OK);

    }

    @PostMapping("upload-company-logo")
    public ResponseEntity<?> uploadCompanyLogo(@RequestParam(value = "file") MultipartFile file, @RequestParam Long clientId) throws IOException {

        return new ResponseEntity<>(adService.uploadCompanyLogo(file, clientId), HttpStatus.OK);

    }

}
