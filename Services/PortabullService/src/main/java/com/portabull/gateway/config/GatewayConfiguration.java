package com.portabull.gateway.config;


import com.portabull.gateway.handlers.ErrorPageHandler;
import org.apache.catalina.Container;
import org.apache.catalina.core.StandardHost;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class GatewayConfiguration {

    static String BASE_URL = "https://portabull.in/APIGateway/";

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> handleErrorPagesOnTomcat() {
        return (factory) ->
                factory.addContextCustomizers(context -> {
                    final Container parent = context.getParent();
                    if (parent instanceof StandardHost) {
                        ((StandardHost) parent).setErrorReportValveClass(ErrorPageHandler.class.getName());
                    }
                });
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

}

