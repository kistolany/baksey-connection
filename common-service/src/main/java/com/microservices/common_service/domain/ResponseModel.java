package com.microservices.common_service.domain;

import com.microservices.common_service.constants.ResponseConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseModel<T> {

    private String datetime;
    private long timestamp;
    private String status;
    private int code;
    private String message;
    private T data;
    private String trace;

    public static <T> ResponseModel<T> success(T data) {
        ResponseModel<T> response = new ResponseModel<T>();
        response.datetime = LocalDateTime.now().toString();
        response.timestamp = System.currentTimeMillis();
        response.status = ResponseConstants.ResponseStatus.SUCCESS.getMessage();
        response.code = ResponseConstants.ResponseStatus.SUCCESS.getValue();
        response.message = "";
        response.trace = "";
        response.data = data;
        return response;
    }

    public static <T> ResponseModel<T> success() {
        return success(null);
    }

}