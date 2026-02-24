package com.microservices.common_service.helpers;

import com.microservices.common_service.constants.ResponseConstants;
import com.microservices.common_service.domain.ResponseModel;
import com.microservices.common_service.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalRestControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApiException.class)
    protected final ResponseEntity<?> handleRestExceptions(final HttpServletRequest request,
                                                               final HttpServletResponse response,
                                                               final ApiException e) {
        logger.error("Catch Rest exception: " + e.getErrorMessage());
        ResponseModel<Object> responseModel = new ResponseModel<>();
        responseModel.setDatetime(LocalDateTime.now().toString());
        responseModel.setTimestamp(new Date().getTime());
        responseModel.setStatus(e.getErrorStatus());
        responseModel.setCode(e.getErrorCode());
        responseModel.setMessage(e.getErrorMessage());
        responseModel.setData(e.getData());
        responseModel.setTrace(e.getMessage());
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected final ResponseEntity<?> handleAllExceptions(final HttpServletRequest request,
                                                          final HttpServletResponse response,
                                                          final Exception e) {
        logger.error("Catch exception: " + e.getMessage());
        ResponseModel<Void> responseModel = new ResponseModel<>();
        responseModel.setDatetime(LocalDateTime.now().toString());
        responseModel.setTimestamp(new Date().getTime());
        responseModel.setStatus(ResponseConstants.ResponseStatus.GENERAL_SERVER_ERROR.getMessage());
        responseModel.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        responseModel.setMessage(ResponseConstants.ResponseMessage.SYSTEM_INTERNAL_SERVER_ERROR.getMessage());
        responseModel.setTrace(e.getMessage());
        return new ResponseEntity<>(responseModel, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        logger.error("Catch handleNoResourceFoundException: " + ex.getMessage());
        ResponseModel<Void> responseModel = new ResponseModel<>();
        responseModel.setDatetime(LocalDateTime.now().toString());
        responseModel.setTimestamp(new Date().getTime());
        responseModel.setStatus(ResponseConstants.ResponseStatus.RESOURCE_NOT_FOUND.getMessage());
        responseModel.setCode(HttpStatus.NOT_FOUND.value());
        responseModel.setMessage(ResponseConstants.ResponseMessage.SYSTEM_RESOURCE_NOT_FOUND.getMessage());
        responseModel.setTrace(ex.getMessage());
        return new ResponseEntity<>(responseModel, HttpStatus.NOT_FOUND);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        logger.error("Catch handleMethodArgumentNotValid: " + ex.getMessage());
        ResponseModel<Void> responseModel = new ResponseModel<>();
        responseModel.setDatetime(LocalDateTime.now().toString());
        responseModel.setTimestamp(new Date().getTime());
        responseModel.setStatus(ResponseConstants.ResponseStatus.BAD_REQUEST.getMessage());
        responseModel.setCode(ResponseConstants.ResponseStatus.BAD_REQUEST.getValue());
        responseModel.setMessage(ResponseConstants.ResponseMessage.SYSTEM_INVALID_REQUEST.getMessage());
        responseModel.setTrace(ex.getMessage());
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        if (!CollectionUtils.isEmpty(fieldErrors)) {
            responseModel.setStatus(ResponseConstants.ResponseStatus.GENERAL_SERVER_ERROR.getMessage());
            responseModel.setCode(ResponseConstants.ResponseStatus.GENERAL_SERVER_ERROR.getValue());
            responseModel.setMessage(fieldErrors.get(0).getDefaultMessage());
            return new ResponseEntity<>(responseModel, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        logger.error("Catch handleHttpMessageNotReadable: " + ex.getMessage());
        ResponseModel<Void> responseModel = new ResponseModel<>();
        responseModel.setDatetime(LocalDateTime.now().toString());
        responseModel.setTimestamp(new Date().getTime());
        responseModel.setStatus(ResponseConstants.ResponseStatus.BAD_REQUEST.getMessage());
        responseModel.setCode(HttpStatus.BAD_REQUEST.value());
        responseModel.setMessage(ResponseConstants.ResponseMessage.SYSTEM_BAD_REQUEST.getMessage());
        responseModel.setTrace(ex.getMessage());
        return new ResponseEntity<>(responseModel, HttpStatus.BAD_REQUEST);
    }
}
