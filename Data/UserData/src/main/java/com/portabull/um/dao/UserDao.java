package com.portabull.um.dao;

import com.portabull.response.PortableResponse;

import java.util.Map;

public interface UserDao {

    public PortableResponse getUsers(String userName, boolean retriveCompleteUsers);

    public PortableResponse getUserReports();

    PortableResponse unlockUserAccount(Long valueOf);

    public PortableResponse lockUnlockAccount(String userName, String type);

    public PortableResponse userRegistered();

    public PortableResponse registerUserData(Map<String, String> notifyDetails);

    PortableResponse lockUnlockCompleteUsers(Integer flag);

    PortableResponse getLastLoggedTime();
}
