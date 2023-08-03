package com.portabull.utils.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * Base Class for all bean configuration settings
 */

public class BeanConfiguration {

    @Autowired
    ApplicationContext applicationContext;

}
