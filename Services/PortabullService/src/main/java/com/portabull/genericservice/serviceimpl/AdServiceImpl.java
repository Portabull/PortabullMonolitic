package com.portabull.genericservice.serviceimpl;

import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.generic.dao.CommonDao;
import com.portabull.generic.models.ads.AdCompanyInformation;
import com.portabull.genericservice.models.CompanyDetails;
import com.portabull.genericservice.service.AdService;
import com.portabull.payloads.KeyValueMapping;
import com.portabull.response.PortableResponse;
import com.portabull.utils.commonutils.CommonUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class AdServiceImpl implements AdService {

    @Autowired
    CommonDao commonDao;

    @Override
    public PortableResponse saveCompanyDetails(CompanyDetails companyDetails) {

        AdCompanyInformation adCompanyInformation = commonDao.findEntity(AdCompanyInformation.class, "companyName", companyDetails.getCompanyName());

        if (adCompanyInformation == null) {
            adCompanyInformation = new AdCompanyInformation();
            adCompanyInformation.setCreatedBy(CommonUtils.getLoggedInUserId());
            adCompanyInformation.setCreatedDate(new Date());
        }

        companyDetails.setClientId(adCompanyInformation.getClientId());

        BeanUtils.copyProperties(companyDetails, adCompanyInformation);

        adCompanyInformation.setUpdatedBy(CommonUtils.getLoggedInUserId());

        adCompanyInformation.setUpdatedDate(new Date());

        commonDao.saveOrUpdateEntity(adCompanyInformation);

        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, adCompanyInformation.getClientId());

    }

    @Override
    public PortableResponse getCompanyDetails(Long pageNo, Long resultSize) {

        Map<String, Object> response = new HashMap<>();

        List<AdCompanyInformation> adCompanyInformations = commonDao.execueQuery(" FROM AdCompanyInformation ORDER BY updatedDate DESC",
                null, pageNo.intValue(), resultSize.intValue());

        if (CollectionUtils.isEmpty(adCompanyInformations)) {
            response.put("count", 0);
            response.put("companies", Collections.emptyList());
            return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, response);
        }

        List<CompanyDetails> companyResponse = new ArrayList<>();
        adCompanyInformations.forEach(company -> {
            CompanyDetails companyDetails = new CompanyDetails();
            BeanUtils.copyProperties(company, companyDetails);
            companyResponse.add(companyDetails);
        });

        response.put("count", commonDao.execueUniqueQuery("SELECT COUNT(clientId) FROM AdCompanyInformation", null));
        response.put("companies", companyResponse);
        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, response);
    }

    @Override
    public PortableResponse searchCompanies(String search) {

        Map<String, Object> response = new HashMap<>();

        search = "%" + search.toLowerCase() + "%";

        Map<String, Object> params = new KeyValueMapping<String, Object>().setKeys("companyName", "companyEmail", "companyMobile", "companyCity", "companyState", "companyZip").setValues(
                search, search, search, search, search, search
        ).getResult();

        List<AdCompanyInformation> adCompanyInformations = commonDao.execueQuery(" FROM AdCompanyInformation WHERE lower(companyName) like :companyName OR lower(companyEmail) like :companyEmail OR lower(companyMobile) like :companyMobile OR lower(companyCity) like :companyCity OR lower(companyState) like :companyState OR lower(companyZip) like :companyZip",
                params);

        List<CompanyDetails> companyResponse = new ArrayList<>();
        adCompanyInformations.forEach(company -> {
            CompanyDetails companyDetails = new CompanyDetails();
            BeanUtils.copyProperties(company, companyDetails);
            companyResponse.add(companyDetails);
        });

        response.put("count", companyResponse.size());
        response.put("companies", companyResponse);
        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, response);
    }

    @Override
    public PortableResponse deleteCompanies(List<Long> companies) {

        List<Long> companyIds = new ArrayList<>();

        for (int i = 0; i < companies.size(); i++) {
            Object aa = companies.get(i);
            companyIds.add(Long.valueOf(aa.toString()));
        }

        commonDao.execute("DELETE FROM AdCompanyInformation WHERE clientId IN (:clientIds)",
                new KeyValueMapping<String, Object>().setKeys("clientIds").setValues(companyIds).getResult());

        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, null);
    }
}
