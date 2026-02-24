package com.microservices.common_service.exception;

import com.microservices.common_service.constants.ResponseConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApiException extends RuntimeException {

    private String errorStatus;
    private int errorCode;
    private String errorMessage;
    private Object data;

    public ApiException(String errorStatus, int errorCode, String errorMessage, Object data) {
        this.errorStatus = errorStatus;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.data = data;
    }

    public ApiException(ResponseConstants.ResponseStatus status, String errorMessage, Object data) {
        this.errorStatus = status.getMessage();
        this.errorCode = status.getValue();
        this.errorMessage = errorMessage;
        this.data = data;
    }

    public ApiException(ResponseConstants.ResponseStatus status, String errorMessage) {
        this.errorStatus = status.getMessage();
        this.errorCode = status.getValue();
        this.errorMessage = errorMessage;
    }

    public ApiException(String errorMessage) {
        this.errorStatus = ResponseConstants.ResponseStatus.GENERAL_SERVER_ERROR.getMessage();
        this.errorCode = ResponseConstants.ResponseStatus.GENERAL_SERVER_ERROR.getValue();
        this.errorMessage = errorMessage;
    }

}
