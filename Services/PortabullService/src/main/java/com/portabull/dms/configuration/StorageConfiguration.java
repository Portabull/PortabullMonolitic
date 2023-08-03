package com.portabull.dms.configuration;

import com.portabull.dms.documentstoragemodule.DocumentLocalStorage;
import com.portabull.dms.documentstoragemodule.DocumentStorageModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.File;

@Configuration
public class StorageConfiguration {

    static Logger logger = LoggerFactory.getLogger(StorageConfiguration.class);

    @Value("${dms.storage.classtype}")
    private String classType;

    /**
     * Local Storage Env
     */

    @Value("${local.storage.location}")
    String localStorageLocation;


    /**
     * Local Storage Configuration
     */

    @Bean
    public void configureLocalStorageLocation() throws ClassNotFoundException {
        if (DocumentLocalStorage.class.getSimpleName().equalsIgnoreCase(Class.forName(classType).getSimpleName())) {
            File file = new File(localStorageLocation);
            if (!file.exists()) {
                if (!file.mkdirs())
                    throw new RuntimeException("Path Creation Failed Please check the permissions for the given path:" + file.getAbsoluteFile());

                logger.info("Local Storage Path Created");
            }
        }
    }

    /**
     * It decides what type of storage module dms stores
     *
     * @return
     * @throws ClassNotFoundException
     */

    @Bean
    public DocumentStorageModule configureStorageModule(@Autowired ApplicationContext applicationContext) throws ClassNotFoundException {

        if (StringUtils.isEmpty(classType)) {
            return applicationContext.getBean(DocumentLocalStorage.class);
        }

        return (DocumentStorageModule) applicationContext.getBean(Class.forName(classType));
    }


}
