package com.portabull.generic.daoimpl;

import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.dbutils.HibernateUtils;
import com.portabull.generic.dao.UserAccountDao;
import com.portabull.generic.models.UserAccountCalculationDetails;
import com.portabull.response.PortableResponse;
import com.portabull.utils.commonutils.CommonUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Repository
public class UserAccountDaoImpl implements UserAccountDao {

    @Autowired
    HibernateUtils hibernateUtils;

    @Override
    public PortableResponse saveUserAccount(Map<String, Object> payload) {

        UserAccountCalculationDetails userAccountDetails = new UserAccountCalculationDetails();

        userAccountDetails.setCreatedDate(new Date());

        userAccountDetails.setDescription(StringUtils.isEmpty(payload.get("desc")) ? "" : payload.get("desc").toString());

        userAccountDetails.setTittle(payload.get("tittle").toString());

        userAccountDetails.setUserID(CommonUtils.getLoggedInUserId());

        userAccountDetails.setProfitLoss(StringUtils.isEmpty(payload.get("profitLossData")) ? 0.0 : Double.valueOf(payload.get("profitLossData").toString()));

        userAccountDetails.setTotalAmount(StringUtils.isEmpty(payload.get("total")) ? 0.0 : Double.valueOf(payload.get("total").toString()));

        userAccountDetails.setUpdatedDate(new Date());

        hibernateUtils.saveOrUpdateEntity(userAccountDetails);

        return new PortableResponse("Account created successfully", StatusCodes.C_200, PortableConstants.SUCCESS, null);
    }

    @Override
    public PortableResponse getUserAccounts(Long pageNo, Long resultSize) {

        List<UserAccountCalculationDetails> accounts;

        Number count = 0;

        PortableResponse response = new PortableResponse();

        try (Session session = hibernateUtils.getSession()) {

            accounts = session.createQuery(" FROM UserAccountCalculationDetails where userID=:userID ORDER BY updatedDate DESC").setFirstResult(pageNo.intValue()).setMaxResults(resultSize.intValue()).setParameter("userID", CommonUtils.getLoggedInUserId()).list();

            count = (Number) session.createQuery("SELECT COUNT(calId) FROM UserAccountCalculationDetails where userID=:userID").setParameter("userID", CommonUtils.getLoggedInUserId()).uniqueResult();

        }

        Map<String, Object> data = new HashMap<>();

        data.put("accounts", getUserAccounts(accounts, pageNo));

        data.put("count", count);

        response.setData(data);

        return response;

    }

    @Override
    public PortableResponse searchUserAccount(String search) {

        PortableResponse response = new PortableResponse();

        List<UserAccountCalculationDetails> accounts;

        try (Session session = hibernateUtils.getSession()) {

            accounts = session.createQuery(" FROM UserAccountCalculationDetails WHERE userID=:userID AND (lower(tittle) like :tittle OR lower(description) like :description)")
                    .setParameter("userID", CommonUtils.getLoggedInUserId()).setParameter("tittle", "%" + search.toLowerCase() + "%").setParameter("description", "%" + search.toLowerCase() + "%").list();

        }

        Map<String, Object> data = new HashMap<>();

        data.put("accounts", getUserAccounts(accounts, 0l));

        data.put("count", accounts.size());

        response.setData(data);

        return response;
    }

    @Override
    public PortableResponse deleteUserAccounts(List<Long> accountIds) {
        Transaction tx = null;

        List<Long> accounts = new ArrayList<>();

        for (int i = 0; i < accountIds.size(); i++) {
            Object aa = accountIds.get(i);
            accounts.add(Long.valueOf(aa.toString()));
        }

        try (Session session = hibernateUtils.getSession()) {
            tx = session.beginTransaction();
            session.createQuery("DELETE FROM UserAccountCalculationDetails where calId IN (:calId) AND userID=:userID").setParameter("userID", CommonUtils.getLoggedInUserId()).setParameter("calId", accounts).executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
        }

        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, null);
    }

    private List<Map<String, Object>> getUserAccounts(List<UserAccountCalculationDetails> accounts, Long pageNo) {

        List<Map<String, Object>> userAccounts = new ArrayList<>();

        AtomicReference<Integer> sNO = new AtomicReference<>(pageNo.intValue() + 1);

        accounts.forEach(account -> {

            Map<String, Object> data = new HashMap<>();

            data.put("sNo", sNO.get().longValue());

            data.put("id", account.getCalId());

            data.put("tittle", account.getTittle());

            data.put("desc", account.getDescription());

            data.put("createdDate", account.getCreatedDate());

            data.put("updatedDate", account.getUpdatedDate());

            data.put("total", account.getTotalAmount());

            data.put("profitLossData", account.getProfitLoss());

            userAccounts.add(data);

            sNO.set(sNO.get() + 1);

        });

        return userAccounts;

    }

}
