package com.bijiabao.config;

import com.bijiabao.patterns.strategy.PredictionStrategyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * 应用配置类
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    
    private final PredictionStrategyManager strategyManager;

    /**
     * 初始化策略管理器
     */
    @Bean
    CommandLineRunner initializeStrategies() {
        return args -> {
            log.info("初始化预测策略管理器...");
            strategyManager.initializeStrategies();
            log.info("预测策略管理器初始化完成");
        };
    }

    /**
     * CORS配置
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:*", "http://127.0.0.1:*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization", "X-Requested-With"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
} 