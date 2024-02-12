package com.portabull.genericservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.portabull.genericservice.service.ExternalJobService;
import com.portabull.response.PortableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("gs/job")
public class ExternalJobController {

    @Autowired
    ExternalJobService jobService;

    @PostMapping("send-email")
    public ResponseEntity<PortableResponse> sendEmail(@RequestBody Map<String, Object> mailPayload) {

        return new ResponseEntity<>(jobService.sendEmail(mailPayload), HttpStatus.OK);

    }

    @PostMapping("execute-rest-api")
    public ResponseEntity<PortableResponse> executeRestAPI(@RequestBody List<Map<String, Object>> restPayloads) throws JsonProcessingException {

        return new ResponseEntity<>(jobService.executeRestAPI(restPayloads), HttpStatus.OK);

    }

    @PostMapping("execute-dynamic-code")
    public ResponseEntity<PortableResponse> executeCode(@RequestBody Map<String, String> codePayload) throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {

        return new ResponseEntity<>(jobService.executeCode(codePayload), HttpStatus.OK);

    }


}
