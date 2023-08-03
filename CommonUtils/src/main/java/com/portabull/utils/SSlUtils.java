package com.portabull.utils;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.function.Supplier;

@Component
public class SSlUtils {

    private static RestTemplateBuilder restTemplateBuilder;

    @Autowired
    public void setRestTemplateBuilder(RestTemplateBuilder restTemplateBuilder) {
        SSlUtils.restTemplateBuilder = restTemplateBuilder;
    }

    @Bean
    public RestTemplateBuilder buildNoHostVerifier() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();

        SSLConnectionSocketFactory scsf = new SSLConnectionSocketFactory(SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build(), NoopHostnameVerifier.INSTANCE);

        HttpClient client = HttpClients.custom().setSSLSocketFactory(scsf).build();

        Supplier<ClientHttpRequestFactory> clientHttpRequestFactorySupplier = new Supplier<ClientHttpRequestFactory>() {
            @Override
            public ClientHttpRequestFactory get() {
                return new HttpComponentsClientHttpRequestFactory(client);
            }
        };

        return restTemplateBuilder.requestFactory(clientHttpRequestFactorySupplier);
    }

    public static RestTemplate getNoHostVerifierRestTemplate() {
        return restTemplateBuilder.build();
    }

}
