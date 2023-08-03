package com.portabull.audit.utils;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.EntityMode;
import org.hibernate.Transaction;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Iterator;

@Component(value = "HibernateInterceptorConfig")
public class HibernateInterceptorConfig extends EmptyInterceptor {

    @Autowired
    HibernateAuditHelper hibernateAuditHelper;

    private static Logger logger = LoggerFactory.getLogger(HibernateInterceptorConfig.class);

    @Override
    public boolean onSave(Object entity,
                          Serializable id,
                          Object[] state,
                          String[] propertyNames,
                          Type[] types) {

        new Thread(() ->
                hibernateAuditHelper.saveAuditDetails(entity, HibernateAuditHelper.SAVE)
        ).start();

        logger.info("onSave:Method------------------------------> called");
        return super.onSave(entity, id, state, propertyNames, types);

    }


    @Override
    public void onDelete(
            Object entity,
            Serializable id,
            Object[] state,
            String[] propertyNames,
            Type[] types) {

        new Thread(() ->
                hibernateAuditHelper.saveAuditDetails(entity, HibernateAuditHelper.DELETE)
        ).start();

        logger.info("onDelete:Method------------------------------> called");
        super.onDelete(entity, id, state, propertyNames, types);
    }

    @Override
    public boolean onFlushDirty(
            Object entity,
            Serializable id,
            Object[] currentState,
            Object[] previousState,
            String[] propertyNames,
            Type[] types) {
        logger.info("onFlushDirty:Method------------------------------> called");
        return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    }

    @Override
    public boolean onLoad(
            Object entity,
            Serializable id,
            Object[] state,
            String[] propertyNames,
            Type[] types) {
        logger.info("onLoad:Method------------------------------> called");

        hibernateAuditHelper.saveAuditDetails(entity, HibernateAuditHelper.GET);

        return super.onLoad(entity, id, state, propertyNames, types);
    }

    @Override
    public void postFlush(Iterator entities) {
        logger.info("postFlush:Method------------------------------> called");
        super.postFlush(entities);
    }

    @Override
    public void preFlush(Iterator entities) {
        logger.info("preFlush:Method------------------------------> called");
        super.preFlush(entities);
    }

    @Override
    public Boolean isTransient(Object entity) {
        logger.info("isTransient:Method------------------------------> called");
        return super.isTransient(entity);
    }

    @Override
    public Object instantiate(String entityName, EntityMode entityMode, Serializable id) {
        logger.info("instantiate:Method------------------------------> called");
        return super.instantiate(entityName, entityMode, id);
    }

    @Override
    public int[] findDirty(
            Object entity,
            Serializable id,
            Object[] currentState,
            Object[] previousState,
            String[] propertyNames,
            Type[] types) {
        logger.info("findDirty:Method------------------------------> called");
        return super.findDirty(entity, id, currentState, previousState, propertyNames, types);
    }

    @Override
    public String getEntityName(Object object) {
        logger.info("getEntityName:Method------------------------------> called");
        return super.getEntityName(object);
    }

    @Override
    public Object getEntity(String entityName, Serializable id) {
        logger.info("getEntity:Method------------------------------> called");
        return super.getEntity(entityName, id);
    }

    @Override
    public void afterTransactionBegin(Transaction tx) {
        logger.info("afterTransactionBegin:Method------------------------------> called");
        super.afterTransactionBegin(tx);
    }

    @Override
    public void afterTransactionCompletion(Transaction tx) {
        logger.info("afterTransactionCompletion:Method------------------------------> called");
        super.afterTransactionCompletion(tx);
    }

    @Override
    public void beforeTransactionCompletion(Transaction tx) {
        logger.info("beforeTransactionCompletion:Method------------------------------> called");
        super.beforeTransactionCompletion(tx);
    }

    @Override
    public String onPrepareStatement(String sql) {

        logger.info("onPrepareStatement:Method------------------------------> called");
        logger.info("Query: {}", sql);
        return super.onPrepareStatement(sql);
    }

    @Override
    public void onCollectionRemove(Object collection, Serializable key) throws CallbackException {
        logger.info("onCollectionRemove:Method------------------------------> called");
        super.onCollectionRemove(collection, key);
    }

    @Override
    public void onCollectionRecreate(Object collection, Serializable key) throws CallbackException {
        logger.info("onCollectionRecreate:Method------------------------------> called");
        super.onCollectionRecreate(collection, key);
    }

    @Override
    public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException {
        logger.info("onCollectionUpdate:Method------------------------------> called");
        super.onCollectionUpdate(collection, key);
    }


}
