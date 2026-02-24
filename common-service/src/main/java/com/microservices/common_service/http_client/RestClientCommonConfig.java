package com.microservices.common_service.http_client;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@AutoConfiguration
@RequiredArgsConstructor
public class RestClientCommonConfig {


    @Bean(name = "restClientCommonInternal")
    public RestClient restClientCommonInternal() {
        var base = new SimpleClientHttpRequestFactory();
        base.setConnectTimeout(10_000);
        base.setReadTimeout(15_000);
        // IMPORTANT: allows interceptor to read response body without breaking downstream deserialization
        var buffering = new BufferingClientHttpRequestFactory(base);
        return RestClient.builder()
                .requestFactory(buffering)
                .build();
    }
}
