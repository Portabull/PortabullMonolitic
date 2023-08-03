package com.portabull.audit.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portabull.audit.models.AuditDetails;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HibernateAuditHelper {

    public static final String SAVE = "SAVE";

    public static final String UPDATE = "UPDATE";

    public static final String DELETE = "DELETE";

    public static final String GET = "GET";

    public static final String QUERY = "QUERY";

    public static final String AUDIT_DETAILS = "AuditDetails";

    private static Logger logger = LoggerFactory.getLogger(HibernateAuditHelper.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return this.sessionFactory.openSession();
    }

    public void closeSession(Session session) {
        if (session != null) {
            session.close();
        }
    }

    public void saveAuditDetails(Object entity, String operationType) {
        try {
            if (HibernateAuditHelper.AUDIT_DETAILS.equalsIgnoreCase(entity.getClass().getSimpleName()))
                return;

            log(entity, operationType);

            this.saveEntity(
                    new AuditDetails()
                            .setEntityName(
                                    entity.getClass()
                                            .getSimpleName()
                            )
                            .setOperationType(operationType)
                            .setData(objectMapper.writeValueAsString(entity))
            );
        } catch (JsonProcessingException e) {
            logger.error("While converting entity to String it throws error");
        } catch (Exception ex) {
            logger.error("While converting entity to String it throws error", ex);
        }

    }

    private void log(Object entity, String operationType) {
        logger.info("HibernateAuditHelper:saveAuditDetails() :started :operationType:----->> {} : entityName: {}",
                operationType, entity.getClass().getSimpleName());
    }

    private <T> T saveEntity(T entity) {
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
}
