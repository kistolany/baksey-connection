package com.microservices.product_service.config;

import feign.codec.Encoder;
import org.springframework.context.annotation.Bean;
import feign.form.spring.SpringFormEncoder;

public class FeignMultipartSupportConfig {

    @Bean
    public Encoder feignFormEncoder() {
        return new SpringFormEncoder();
    }
}

