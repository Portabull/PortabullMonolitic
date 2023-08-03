package com.portabull.generic.dao;

import com.portabull.response.PortableResponse;

import java.util.List;
import java.util.Map;

public interface UserAccountDao {

    public PortableResponse saveUserAccount(Map<String, Object> payload);

    public PortableResponse getUserAccounts(Long pageNo, Long resultSize);

    public PortableResponse searchUserAccount(String search);

    public PortableResponse deleteUserAccounts(List<Long> accountIds);
}
