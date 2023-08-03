package com.portabull.dbutils;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

@Configuration
@EnableTransactionManagement
@Profile({"mysql", "postgres", "oracle"})
public class DataBaseConfiguration {


    @Value("${hibernate.dialect}")
    private String dialect;

    @Value("${entitymanager.packagesToScan}")
    private String packagesToScan;

    @Value("${hibernate.hbm2ddl.auto}")
    private String autoDDL;

    @Value("${hibernate.show_sql}")
    private String showSql;

    @Value("${hibernate.id.new_generator_mappings}")
    private String hibernateIdNewGeneratorMappings;

    @Bean
    public LocalSessionFactoryBean sessionFactory(HikariDataSource dataSource) {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        if (packagesToScan != null && packagesToScan.contains(",")) {
            sessionFactoryBean.setPackagesToScan(packagesToScan.split(","));
        }
        sessionFactoryBean.setPackagesToScan(packagesToScan);
        Properties properties = dataSource.getDataSourceProperties();
        properties.put("hibernate.dialect", dialect);
        properties.put("hibernate.hbm2ddl.auto", autoDDL);
        properties.put("hibernate.show_sql", showSql);
        properties.put("hibernate.id.new_generator_mappings", hibernateIdNewGeneratorMappings);
        sessionFactoryBean.setHibernateProperties(properties);
        dataSource.setLeakDetectionThreshold(60 * (long) 10000);
        sessionFactoryBean.setDataSource(dataSource);
        return sessionFactoryBean;
    }

    @Bean
    public HibernateTransactionManager transactionManager(HikariDataSource dataSource) {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(this.sessionFactory(dataSource).getObject());
        return transactionManager;
    }


}
