package com.microservices.attachment_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean(name = "restClientCommonInternal")
    public RestClient restClientCommonInternal() {
        return RestClient.builder().build();
    }
}