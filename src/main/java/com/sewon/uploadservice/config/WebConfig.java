package com.sewon.uploadservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@RequiredArgsConstructor
@ConfigurationProperties(prefix = "front")
public class WebConfig implements WebMvcConfigurer {

    private final String host;
    private final int port;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String url = "http://" + host + ":" + port;
        registry.addMapping("/**") // 1. 모든 API 경로에 대해
            .allowedOrigins(url) // 2. Vue 앱의 주소(Origin)만 허용
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // 3. 허용할 HTTP 메서드
            .allowedHeaders("*") // 4. 모든 헤더 허용
            .allowCredentials(true) // 5. 쿠키/인증 정보 허용
            .maxAge(3600); // 6. (선택) Preflight 요청 캐시 시간(초)
    }
}
