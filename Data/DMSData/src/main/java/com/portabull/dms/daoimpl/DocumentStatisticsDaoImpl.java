package com.portabull.dms.daoimpl;

import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.dbutils.HibernateUtils;
import com.portabull.dms.dao.DocumentStatisticsDao;
import com.portabull.response.PortableResponse;
import com.portabull.utils.commonutils.CommonUtils;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DocumentStatisticsDaoImpl implements DocumentStatisticsDao {

    @Autowired
    HibernateUtils hibernateUtils;

    @Override
    public PortableResponse getStatistics() {

        Map<String, Object> response = new HashMap<>();

        try (Session session = hibernateUtils.getSession()) {

            List<Object> fileStatistics = session.createSQLQuery(
                    "SELECT LOWER(split_part(name, '.', 2)) as exe ,(sum(size)/1024)/1024 as total_size from dms.document_data where user_id=:user_id group by LOWER(split_part(name, '.', 2))"
            ).setParameter("user_id", CommonUtils.getLoggedInUserId()).list();

            Double storageSize = (Double) session.createQuery(
                    "SELECT storageSize FROM UserDocumentStorage WHERE userId=:userId"
            ).setParameter("userId", CommonUtils.getLoggedInUserId()).uniqueResult();

            response.put("fileStatistics", fileStatistics);

            response.put("storageSize", storageSize);

            return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, response);
        }
    }

}
