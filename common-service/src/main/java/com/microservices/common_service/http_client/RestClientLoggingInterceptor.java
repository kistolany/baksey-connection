package com.microservices.common_service.http_client;

import com.microservices.common_service.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Set;

public class RestClientLoggingInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger log = LoggerFactory.getLogger(RestClientLoggingInterceptor.class);
    private static final Set<String> SENSITIVE_HEADERS =
            Set.of("authorization", "cookie", "set-cookie");

    // Prevent huge logs
    private static final int MAX_LOG_BODY_CHARS = 7000;

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution) throws IOException {
        long startNanos = System.nanoTime();
        RestClientRequest restClientRequest = logRequest(startNanos, request, body);
        ClientHttpResponse response = execution.execute(request, body);
        long endNanos = System.nanoTime();
        long tookMs = Duration.ofNanos(endNanos - startNanos).toMillis();
        logResponse(restClientRequest, response, endNanos, tookMs);
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Unexpected response status: {}", response.getStatusCode());
//            telegramErrorEventPublisher.publish(TelegramErrorEvent.builder()
//                    .env("Took(s) " + tookMs)
//                    .clazzName(this.getClass().getSimpleName())
//                    .serviceName("Unexpected " + response.getStatusCode())
//                    .text(restClientRequest.getRequestBody())
//                    .build());
        }
        return response;
    }

    private RestClientRequest logRequest(long startNanos, HttpRequest request, byte[] body) {
        RestClientRequest restClientRequest = RestClientRequest.builder()
                .requestMethod(request.getMethod().name())
                .requestUrl(request.getURI().toString())
                .requestHeaders(formatHeaders(request.getHeaders()))
                .requestId(MDC.get("LOG_REQUEST_ID"))
                .startNanos(startNanos)
                .build();
        if (body != null && body.length > 0) {
            restClientRequest.setRequestBody(truncate(new String(body, StandardCharsets.UTF_8)));
        }
        log.info("➡️ Request => {}", CommonUtils.toJsonString(restClientRequest));
        return restClientRequest;
    }

    private void logResponse(RestClientRequest restClientRequest, ClientHttpResponse response, long endNanos, long tookMs) throws IOException {
        RestClientResponse restClientResponse = RestClientResponse.builder()
                .request(restClientRequest)
                .status(response.getStatusCode().value())
                .statusText(HttpStatus.valueOf(response.getStatusCode().value()).name())
                .tookMs(tookMs)
                .responseHeaders(formatHeaders(response.getHeaders()))
                .endNanos(endNanos)
                .build();
        byte[] responseBody = StreamUtils.copyToByteArray(response.getBody());
        if (responseBody.length > 0) {
            restClientResponse.setResponseBody(truncate(new String(responseBody, StandardCharsets.UTF_8)));
        }
        log.info("⬅️ Response (took {}) => {}", tookMs, CommonUtils.toJsonString(restClientResponse));
    }

    private HashMap<String, String> formatHeaders(HttpHeaders headers) {
        HashMap<String, String> map = new HashMap<>();
        headers.forEach((key, values) -> {
            if (SENSITIVE_HEADERS.contains(key.toLowerCase())) {
                map.put(key, "*********");
            } else {
                map.put(key, String.join(",", values));
            }
        });
        return map;
    }

    private String truncate(String s) {
        if (s == null) return null;
        return s.length() <= MAX_LOG_BODY_CHARS ? s : s.substring(0, MAX_LOG_BODY_CHARS) + "...(truncated)";
    }
}