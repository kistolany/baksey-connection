package com.microservices.common_service.http_client;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class RestClientRequest {
    private String requestId;
    private String requestUrl;
    private String requestMethod;
    private Map<String, String> requestHeaders;
    private String requestBody;
    private long startNanos;
}
