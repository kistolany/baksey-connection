package com.microservices.common_service.constants;

import lombok.Getter;

public class ResponseConstants {

    @Getter
    public enum ResponseStatus {

        SUCCESS(200, "success"),
        BAD_REQUEST(400, "bad_request"),
        UNAUTHORIZED(401, "unauthorized"),
        FORBIDDEN(403, "no_permission_access_is_denied"),
        NOT_FOUND(404, "not_found"),
        RESOURCE_NOT_FOUND(404, "resource_not_found"),
        DATA_NOT_FOUND(404, "data_not_found"),
        GENERAL_SERVER_ERROR(500, "general_server_error"),
        INTERNAL_SERVER_ERROR(500, "internal_server_error"),
        ;

        private final int value;
        private final String message;

        ResponseStatus(int value, String message) {
            this.value = value;
            this.message = message;
        }

    }

    @Getter
    public enum ResponseMessage {

        SYSTEM_DATA_FOUND("Data found."),
        SYSTEM_DATA_NOT_FOUND("Data not found."),
        SYSTEM_RESOURCE_NOT_FOUND("Resource not found."),
        SYSTEM_INTERNAL_SERVER_ERROR("Internal server error."),
        SYSTEM_INVALID_REQUEST("Invalid request."),
        SYSTEM_INVALID_REQUEST_HEADER("Invalid request header."),
        SYSTEM_REQUIRED_REQUEST_HEADER("Request header is required."),
        SYSTEM_BAD_REQUEST("Bad request."),
        SYSTEM_UNAUTHORIZED("Unauthorized."),
        SYSTEM_INVALID_CREDENTIALS("Invalid credentials."),
        SYSTEM_SESSION_EXPIRED("Session is expired."),
        SYSTEM_SOMETHING_WENT_WRONG("Something went wrong."),
        ;

        private final String message;

        ResponseMessage(String message) {
            this.message = message;
        }

    }
}
