package com.kartly.order_service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class FeignClientConfig {

    @Bean
    public RequestInterceptor serviceAuthInterceptor(ServiceTokenProvider serviceTokenProvider) {
        return (RequestTemplate template) -> template.header("Authorization", "Bearer " + serviceTokenProvider.getServiceToken());
    }
}
