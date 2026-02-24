package com.microservices.common_service.http_client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class RestClientCommon {

    private final RestClient restClient;

    public RestClientCommon(@Qualifier("restClientCommonInternal") RestClient restClient) {
        this.restClient = restClient;
    }

    public <T> T get(String url, Map<String, String> headers, Class<T> responseType) {
        return restClient.get()
                .uri(url)
                .headers(h -> applyHeaders(h, headers))
                .retrieve()
                .body(responseType);
    }

    public <T> T get(String url, Map<String, String> headers, ParameterizedTypeReference<T> responseType) {
        return restClient.get()
                .uri(url)
                .headers(h -> applyHeaders(h, headers))
                .retrieve()
                .body(responseType);
    }

    public <T, B> T post(String url, B body, Map<String, String> headers, Class<T> responseType) {
        return restClient.post()
                .uri(url)
                .headers(h -> applyHeaders(h, headers))
                .body(body)
                .retrieve()
                .body(responseType);
    }

    public <T, B> T post(String url, B body, Map<String, String> headers, ParameterizedTypeReference<T> responseType) {
        return restClient.post()
                .uri(url)
                .headers(h -> applyHeaders(h, headers))
                .body(body)
                .retrieve()
                .body(responseType);
    }

    public <T, B> T put(String url, B body, Map<String, String> headers, Class<T> responseType) {
        return restClient.put()
                .uri(url)
                .headers(h -> applyHeaders(h, headers))
                .body(body)
                .retrieve()
                .body(responseType);
    }

    public <T, B> T put(String url, B body, Map<String, String> headers, ParameterizedTypeReference<T> responseType) {
        return restClient.put()
                .uri(url)
                .headers(h -> applyHeaders(h, headers))
                .body(body)
                .retrieve()
                .body(responseType);
    }

    private void applyHeaders(HttpHeaders httpHeaders, Map<String, String> headers) {
        if (headers != null) headers.forEach(httpHeaders::add);
    }
}