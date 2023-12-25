package com.portabull.genericservice.service;

import com.portabull.genericservice.models.CompanyDetails;
import com.portabull.response.PortableResponse;

import java.util.List;

public interface AdService {

    public PortableResponse saveCompanyDetails(CompanyDetails companyDetails);

    public PortableResponse getCompanyDetails(Long pageNo, Long resultSize);

    PortableResponse searchCompanies(String search);

    PortableResponse deleteCompanies(List<Long> companies);
}
