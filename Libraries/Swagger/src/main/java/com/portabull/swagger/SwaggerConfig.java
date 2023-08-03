package com.portabull.swagger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.PostConstruct;

@Configuration
@ConditionalOnExpression(value = "${enable-swagger-documentation:false}")
@EnableSwagger2
public class SwaggerConfig {

    @Autowired
    Environment environment;

    private static final Logger logger = LoggerFactory.getLogger(SwaggerConfig.class);

    @PostConstruct
    public void logSwaggerConfigMessage() {
        if (Boolean.TRUE.equals(environment.getProperty("enable-swagger-documentation", boolean.class))) {
            logger.warn("Swagger Enabled for this Application, please disable if it is not dev env.",
                    new Throwable("Swagger Enabled for this Application"));
        } else {
            logger.info("Swagger Disabled for this Application");
        }
    }


}