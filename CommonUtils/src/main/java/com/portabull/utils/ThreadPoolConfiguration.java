package com.portabull.utils;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
@Configuration
@EnableAsync
public class ThreadPoolConfiguration {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(100);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(Integer.MAX_VALUE);
        executor.setThreadNamePrefix("PortabullThread-");
        executor.initialize();
        return executor;
    }

    public static int getThreadCountBasedOnNoOfTasks(int noOfTasks) {
        switch (noOfTasks) {
            case 8:
                return 8;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 7:
                return 7;
            case 1:
                return 1;
            case 5:
                return 5;
            case 6:
                return 6;
            case 9:
                return 9;
            default:
                return 10;
        }
    }

}
