package com.portabull.dms.controllers;

import com.portabull.dms.service.DocumentStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("DMS")
public class DocumentStatisticsController {

    @Autowired
    DocumentStatisticsService documentStatisticsService;

    @GetMapping("/get-statistics")
    public ResponseEntity<?> getStatistics() {
        return new ResponseEntity<>(documentStatisticsService.getStatistics(), HttpStatus.OK);
    }

}
