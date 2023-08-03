package com.portabull.genericservice.serviceimpl;

import com.portabull.generic.dao.UserAccountDao;
import com.portabull.genericservice.service.UserAccountService;
import com.portabull.response.PortableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserAccountServiceImpl implements UserAccountService {

    @Autowired
    UserAccountDao userAccountDao;

    @Override
    public PortableResponse saveUserAccount(Map<String, Object> payload) {
        return userAccountDao.saveUserAccount(payload);
    }

    @Override
    public PortableResponse getUserAccounts(Long pageNo, Long resultSize) {
        return userAccountDao.getUserAccounts(pageNo, resultSize);
    }

    @Override
    public PortableResponse searchUserAccount(String search) {
        return userAccountDao.searchUserAccount(search);
    }

    @Override
    public PortableResponse deleteUserAccounts(List<Long> accountIds) {
        return userAccountDao.deleteUserAccounts(accountIds);
    }

}
