package com.portabull.generic.dao;

import com.portabull.generic.models.UserDocumentStorage;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface CommonDao {

    public UserDocumentStorage getUserDocumentStorage();

    public <T> T saveOrUpdateEntity(T data);

    public <T> List<T> execueQuery(String query, Map<String, Object> params);

    public <T> T execueUniqueQuery(String query, Map<String, Object> params);

    public <T> T deleteEntity(Class<T> entity, Serializable primaryId);

    public void deleteEntitys(List<Object> entitys);

    public <T> T findEntity(Class<T> entityClass, String primaryPropertyName, Serializable primaryId);

    public <T> T findEntity(Class<?> entityClass, Serializable primaryId);

    public <T> List<T> loadFullData(Class<T> entityClass);

    public <T> List<T> findEntitiesByCriteria(Class<T> entityClass, String primaryPropertyName, Serializable primaryId);

    public <T> List<T> findEntitiesByCriteria(Class<T> entityClass, String primaryPropertyName1, Serializable primaryId1, String primaryPropertyName2, Serializable primaryId2);

    public <T> List<T> findEntitiesByCriteria(Class<T> entityClass, String primaryPropertyName1, List<?> primaryId1);

    public <T> List<T> executeQuery(String query, Map<String, Object> params, Class<T> expectedReturn);

    public <T> List<T> execueQuery(String query, Map<String, Object> params,Integer pageNo,Integer resultSize);

    public void execute(String query, Map<String, Object> params);

}
