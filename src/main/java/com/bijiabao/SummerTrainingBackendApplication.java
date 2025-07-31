package com.bijiabao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 比价宝后端服务主应用类
 * 设计模式重构版本
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableAsync
@EnableScheduling
public class SummerTrainingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SummerTrainingBackendApplication.class, args);
    }
} 