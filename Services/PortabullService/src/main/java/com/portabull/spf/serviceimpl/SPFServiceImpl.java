package com.portabull.spf.serviceimpl;

import com.portabull.spf.dao.SPFDao;
import com.portabull.spf.service.SPFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SPFServiceImpl implements SPFService {

    @Autowired
    SPFDao spfDao;

}
