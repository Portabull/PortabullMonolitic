package com.portabull.dms.serviceimpl;

import com.portabull.dms.dao.DocumentStatisticsDao;
import com.portabull.dms.service.DocumentStatisticsService;
import com.portabull.response.PortableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentStatisticsServiceImpl implements DocumentStatisticsService {

    @Autowired
    DocumentStatisticsDao documentStatisticsDao;

    @Override
    public PortableResponse getStatistics() {
        return documentStatisticsDao.getStatistics();
    }

}
