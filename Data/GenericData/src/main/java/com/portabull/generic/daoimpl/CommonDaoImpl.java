package com.portabull.generic.daoimpl;

import com.portabull.dbutils.HibernateUtils;
import com.portabull.generic.dao.CommonDao;
import com.portabull.generic.models.UserDocumentStorage;
import com.portabull.utils.commonutils.CommonUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class CommonDaoImpl implements CommonDao {

    @Autowired
    HibernateUtils hibernateUtils;

    @Override
    public UserDocumentStorage getUserDocumentStorage() {
        try (Session session = hibernateUtils.getSession()) {
            return (UserDocumentStorage) session.createQuery(" FROM UserDocumentStorage WHERE userId=:userId").setParameter("userId", CommonUtils.getLoggedInUserId()).uniqueResult();
        }
    }

    @Override
    public <T> T saveOrUpdateEntity(T data) {
        return hibernateUtils.saveOrUpdateEntity(data);
    }

    @Override
    public <T> List<T> execueQuery(String query, Map<String, Object> params) {
        List<T> response;
        try (Session session = hibernateUtils.getSession()) {
            Query queryBuilder = session.createQuery(query);

            if (!CollectionUtils.isEmpty(params)) {
                params.forEach((k, v) -> queryBuilder.setParameter(k, v));
            }
            response = queryBuilder.list();
            return !CollectionUtils.isEmpty(response) ? response : Collections.emptyList();
        }
    }

    @Override
    public <T> T execueUniqueQuery(String query, Map<String, Object> params) {
        try (Session session = hibernateUtils.getSession()) {
            Query queryBuilder = session.createQuery(query);

            if (!CollectionUtils.isEmpty(params)) {
                params.forEach((k, v) -> queryBuilder.setParameter(k, v));
            }
            return (T) queryBuilder.uniqueResult();
        }
    }

    @Override
    public <T> T deleteEntity(Class<T> entity, Serializable primaryId) {
        return hibernateUtils.deleteEntity(entity, primaryId);
    }

    @Override
    public void deleteEntitys(List<Object> entitys) {
        hibernateUtils.deleteEntitys(entitys);
    }

    @Override
    public <T> T findEntity(Class<T> entityClass, String primaryPropertyName, Serializable primaryId) {
        return hibernateUtils.findEntityByCriteria(entityClass, primaryPropertyName, primaryId);
    }

    @Override
    public <T> T findEntity(Class<?> entityClass, Serializable primaryId) {
        return hibernateUtils.findEntity(entityClass, primaryId);
    }

    @Override
    public <T> List<T> loadFullData(Class<T> entityClass) {
        return hibernateUtils.loadFullData(entityClass);
    }

    @Override
    public <T> List<T> findEntitiesByCriteria(Class<T> entityClass, String primaryPropertyName, Serializable primaryId) {
        return hibernateUtils.findEntitiesByCriteria(entityClass, primaryPropertyName, primaryId);
    }

    @Override
    public <T> List<T> findEntitiesByCriteria(Class<T> entityClass, String primaryPropertyName1, Serializable primaryId1, String primaryPropertyName2, Serializable primaryId2) {
        return hibernateUtils.findEntitiesByCriteria(entityClass, primaryPropertyName1, primaryId1, primaryPropertyName2, primaryId2);
    }

    @Override
    public <T> List<T> findEntitiesByCriteria(Class<T> entityClass, String primaryPropertyName1, List<?> primaryId1) {
        try (Session session = hibernateUtils.getSession()) {
            return session.createQuery(new StringBuilder(" FROM ").append(entityClass.getName()).append(" WHERE ").
                    append(primaryPropertyName1).append(" IN (:").append(primaryPropertyName1).append(")")
                    .toString()).setParameter(primaryPropertyName1, primaryId1).list();
        }
    }

    @Override
    public <T> List<T> executeQuery(String query, Map<String, Object> params, Class<T> expectedReturn) {
        List<T> response;
        try (Session session = hibernateUtils.getSession()) {

            Query queryBuilder = session.createQuery(query);

            if (!CollectionUtils.isEmpty(params)) {
                params.forEach((paramKey, paramValue) -> queryBuilder.setParameter(paramKey, paramValue));
            }

            queryBuilder.setResultTransformer(Transformers.aliasToBean(expectedReturn));

            response = queryBuilder.list();

            return !CollectionUtils.isEmpty(response) ? response : Collections.emptyList();
        } catch (Exception e) {
            LoggerFactory.getLogger(CommonDaoImpl.class).error("", e);
        }

        return Collections.emptyList();
    }

}
