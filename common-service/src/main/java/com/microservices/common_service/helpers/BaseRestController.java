//package com.microservices.common_service.helpers;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.microservices.common_service.exeption.RestException;
//import com.microservices.common_service.utils.CommonUtils;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
//
//@Slf4j
//@RestControllerAdvice
//public class BaseRestController extends ResponseEntityExceptionHandler {
//
//    protected String responseData(Object data, HttpServletRequest request, HttpServletResponse response) {
//        log.info("Response (200) Data: {}", CommonUtils.toJsonString(data));
//        try {
//            return ResponseHelpers.responseData(data, request, response);
//        } catch (JsonProcessingException e) {
//            logger.error(e.getMessage(), e);
//            throw new RestException(e.getMessage());
//        }
//    }
//
//    protected String responseSuccess(String message, HttpServletRequest request, HttpServletResponse response) {
//        log.info("Response (200) Success: {}", message);
//        try {
//            return ResponseHelpers.responseSuccess(message, request, response);
//        } catch (JsonProcessingException e) {
//            logger.error(e.getMessage(), e);
//            throw new RestException(e.getMessage());
//        }
//    }
//}
