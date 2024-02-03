package com.portabull;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com")
public class PortabullService {

    public static void main(String[] args) {
        System.out.println();
        SpringApplication.run(PortabullService.class, args);
    }

}
