package com.portabull.um.jobs;

import com.portabull.dbutils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@EnableScheduling
public class UserManagementJobs {

    @Autowired
    HibernateUtils hibernateUtils;

    static Logger logger = LoggerFactory.getLogger(UserManagementJobs.class);

    AtomicInteger threadCount = new AtomicInteger();

    @Scheduled(cron = "1 * * * * *")
    public void cronJobSch() {
        threadCount.incrementAndGet();
        if (threadCount.get() > 5) {
            return;
        }

        synchronized (UserManagementJobs.class) {
            logger.info("UserManagement Job Started::");

            try (Session session = hibernateUtils.getSession()) {

                Transaction transaction = session.beginTransaction();

                session.createQuery("DELETE NotificationUserJWTToken t WHERE t.expiryDate < :currentDate").
                        setParameter("currentDate", new Date().getTime()).executeUpdate();

                transaction.commit();

            }

            logger.info("UserManagement Job Ended::");
            threadCount.decrementAndGet();
        }
    }


}
