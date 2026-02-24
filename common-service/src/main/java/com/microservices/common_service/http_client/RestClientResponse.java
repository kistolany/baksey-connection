package com.microservices.common_service.http_client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class RestClientResponse {
    @JsonIgnore
    private RestClientRequest request;
    private int status;
    private String statusText;
    private long tookMs;
    private long endNanos;
    private Map<String, String> responseHeaders;
    private String responseBody;

    public String getRequestId() {
        return this.request.getRequestId();
    }

    public String getRequestBody() {
        return this.request.getRequestBody();
    }

    public String getRequestMethod() {
        return this.request.getRequestMethod();
    }

    public String getRequestUrl() {
        return this.request.getRequestUrl();
    }

    public Map<String, String> getRequestHeaders() {
        return this.request.getRequestHeaders();
    }
}
