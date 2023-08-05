package com.portabull.dbutils;

import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaQuery;
import java.io.Serializable;
import java.util.List;


@Component
public class HibernateUtils {

    @Autowired
    private SessionFactory sessionFactory;

    @Value("${enable.hibernate.audits}")
    private boolean enableHibernateAudits;

    @Autowired
    @Qualifier(value = "HibernateInterceptorConfig")
    public Object hibernateInterseptor;

    public void setSessionFactory(SessionFactory sf) {
        this.sessionFactory = sf;
    }

    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    public Session getSession() {
        if (this.sessionFactory != null) {
            if (enableHibernateAudits && hibernateInterseptor != null) {
                return this.sessionFactory.withOptions().interceptor((Interceptor) hibernateInterseptor).openSession();
            } else {
                return this.sessionFactory.openSession();
            }
        }
        return null;
    }

    public void closeSession(Session session) {
        if (session != null) {
            session.close();
        }
    }

    public <T> T saveEntity(T entity) {
        Transaction transaction = null;
        try (Session session = this.getSession()) {

            transaction = session.beginTransaction();
            session.save(entity);
            transaction.commit();
            return entity;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public <T> T findEntity(Class<?> entityClass, Serializable primaryId) {
        try (Session session = this.getSession()) {
            return (T) session.get(entityClass, primaryId);
        }
    }

    public <T> List<T> loadFullData(Class<T> entityClass) {
        try (Session session = this.getSession()) {
            CriteriaQuery<T> criteria = session.getCriteriaBuilder().createQuery(entityClass);
            criteria.from(entityClass);
            return session.createQuery(criteria).getResultList();
        }
    }

    public <T> T deleteEntity(Class<T> entity, Serializable primaryId) {
        Transaction tx = null;
        try (Session session = getSession()) {
            tx = session.beginTransaction();
            T dataObject = session.get(entity, primaryId);
            session.delete(dataObject);
            tx.commit();
            return dataObject;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

    public void deleteEntitys(List<Object> entitys) {
        Transaction tx = null;
        try (Session session = getSession()) {
            tx = session.beginTransaction();
            for (Object entity : entitys) {
                session.delete(entity);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

    public <T> List<T> saveOrUpdateEntityList(List<T> entityList) {
        Transaction tx = null;
        try (Session session = this.getSession()) {
            tx = session.beginTransaction();
            for (T entity : entityList) {
                session.saveOrUpdate(entity);
            }
            tx.commit();
            return entityList;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

    public <T> T saveOrUpdateEntity(T entity) {
        Transaction transaction = null;
        try (Session session = this.getSession()) {
            transaction = session.beginTransaction();
            session.saveOrUpdate(entity);
            transaction.commit();
            return entity;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public <T> T saveOrUpdateEntity(Session session, T entity) {
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.saveOrUpdate(entity);
            transaction.commit();
            return entity;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public <T> List<T> findEntitiesByCriteria(Class<T> entityClass, String primaryPropertyName, Serializable primaryId) {
        try (Session session = getSession()) {
            List<T> dataObject = session.createCriteria(entityClass).add(Restrictions.eq(primaryPropertyName, primaryId)).list();
            return dataObject;
        }
    }

    public <T> List<T> findEntitiesByCriteria(Class<T> entityClass, String primaryPropertyName1, Serializable primaryId1, String primaryPropertyName2, Serializable primaryId2) {
        try (Session session = getSession()) {
            List<T> dataObject = session.createCriteria(entityClass).add(Restrictions.eq(primaryPropertyName1, primaryId1))
                    .add(Restrictions.eq(primaryPropertyName2, primaryId2)).list();
            return dataObject;
        }
    }

    public <T> T findEntityByCriteria(Class<T> entityClass, String primaryPropertyName, Serializable primaryId) {
        try (Session session = getSession()) {
            Object dataObject = session.createCriteria(entityClass).add(Restrictions.eq(primaryPropertyName, primaryId))
                    .uniqueResult();
            return (T) dataObject;
        }
    }

    public <T> T findEntityByCriteria(Class<T> entityClass, String primaryPropertyName, Serializable primaryId,String primaryPropertyName1, Serializable primaryId1) {
        try (Session session = getSession()) {
            Object dataObject = session.createCriteria(entityClass)
                    .add(Restrictions.eq(primaryPropertyName, primaryId))
                    .add(Restrictions.eq(primaryPropertyName1, primaryId1))
                    .uniqueResult();
            return (T) dataObject;
        }
    }
}
