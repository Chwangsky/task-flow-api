package com.taskflow.api.common.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); // 실행 대기하는 스레드의 최소 개수
        executor.setMaxPoolSize(10); // 최대 스레드 개수
        executor.setQueueCapacity(500); // 큐 대기열의 용량
        executor.setThreadNamePrefix("Async-"); // 생성되는 스레드의 이름 접두사
        executor.initialize();
        return executor;
    }
}