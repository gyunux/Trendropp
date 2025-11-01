package com.celebstyle.api.common.config;

import java.time.Duration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AppConfig {

    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(10)) // 연결 시도 시간 10초
                .setReadTimeout(Duration.ofSeconds(30))    // 응답 대기 시간 30초
                .build();
    }

    @Bean
    S3Client s3Client() {
        return S3Client.builder()
                .region(Region.AP_NORTHEAST_2) // 서울 리전
                .build();
    }

    @Bean
    MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
