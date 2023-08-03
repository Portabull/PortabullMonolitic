package com.portabull.spf.controllers;

import com.portabull.spf.service.SPFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("spf")
public class SPFController {

    @Autowired
    SPFService spfService;
}
