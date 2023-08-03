package com.portabull.genericservice.serviceimpl;

import com.portabull.generic.dao.GenericDao;
import com.portabull.genericservice.service.GenericService;
import com.portabull.response.PortableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GenericServiceImpl implements GenericService {

    @Autowired
    GenericDao genericDao;

    @Override
    public PortableResponse saveClientContactDetails(Map<String, String> payload) {
        return genericDao.saveClientContactDetails(payload);
    }

    @Override
    public PortableResponse getClientContactDetails() {
        return genericDao.getClientContactDetails();
    }

}
