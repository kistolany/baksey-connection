package com.microservices.common_service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@UtilityClass
public class CommonUtils {

    private static final Set<String> SENSITIVE_FIELDS = Set.of(
            "password",
            "pin",
            "otpCode"
    );

    private static final ObjectMapper mapper = new ObjectMapper()
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    public static String toJsonString(Object object) {
        try {
            Object rawMap = mapper.convertValue(object, Object.class);
            Object maskedObject = maskSensitiveData(rawMap);
            return mapper.writeValueAsString(maskedObject);
        } catch (Exception e) {
            log.error("Error at toJsonString because {}", e.getMessage());
            return null;
        }
    }

    private static Object maskSensitiveData(Object value) {
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> masked = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String key = String.valueOf(entry.getKey());
                if (SENSITIVE_FIELDS.contains(key)) {
                    masked.put(key, "****");
                } else {
                    masked.put(key, maskSensitiveData(entry.getValue()));
                }
            }
            return masked;
        }

        if (value instanceof List<?> list) {
            return list.stream()
                    .map(CommonUtils::maskSensitiveData)
                    .toList();
        }
        return value;
    }
}
