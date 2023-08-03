package com.portabull.genericservice.service;

import com.portabull.response.PortableResponse;

import java.util.List;
import java.util.Map;

public interface UserAccountService {

    public PortableResponse saveUserAccount(Map<String, Object> payload);

    public PortableResponse getUserAccounts(Long pageNo, Long resultSize);

    public PortableResponse searchUserAccount(String search);

    public PortableResponse deleteUserAccounts(List<Long> accountIds);
}
