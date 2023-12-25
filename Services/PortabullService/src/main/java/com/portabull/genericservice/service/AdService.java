package com.portabull.genericservice.service;

import com.portabull.genericservice.models.CompanyDetails;
import com.portabull.response.PortableResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AdService {

    public PortableResponse saveCompanyDetails(CompanyDetails companyDetails);

    public PortableResponse getCompanyDetails(Long pageNo, Long resultSize);

    public PortableResponse getCompanyDetails(Long clientId);
    PortableResponse searchCompanies(String search);

    PortableResponse deleteCompanies(List<Long> companies);

    PortableResponse uploadCompanyLogo(MultipartFile file, Long clientId) throws IOException;
}
