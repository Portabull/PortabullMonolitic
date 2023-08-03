package com.portabull.generic.daoimpl;

import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.dbutils.HibernateUtils;
import com.portabull.generic.dao.GenericDao;
import com.portabull.generic.models.CustomerEnquiry;
import com.portabull.response.PortableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Map;

@Repository
public class GenericDaoImpl implements GenericDao {

    @Autowired
    HibernateUtils hibernateUtils;

    @Override
    public PortableResponse saveClientContactDetails(Map<String, String> payload) {
        CustomerEnquiry customerEnquiry = new CustomerEnquiry();
        customerEnquiry.setCustomerName(payload.get("name"));
        customerEnquiry.setCustomerEmail(payload.get("email"));
        customerEnquiry.setSubject(payload.get("subject"));
        customerEnquiry.setMessage(payload.get("message"));
        customerEnquiry.setMobileNumber(payload.get("contactNumber"));
        customerEnquiry.setEnquiryData(new Date().getTime());
        hibernateUtils.saveOrUpdateEntity(customerEnquiry);
        return new PortableResponse("Your message was notified to admin user, will contact you shortly",
                StatusCodes.C_200, PortableConstants.SUCCESS, null);
    }

    @Override
    public PortableResponse getClientContactDetails() {
        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS,
                hibernateUtils.loadFullData(CustomerEnquiry.class));
    }
}
