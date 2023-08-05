package com.portabull.oauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com")
public class OAuthSecurity {

    public static void main(String args[]) {
        SpringApplication.run(OAuthSecurity.class, args);
    }

}
