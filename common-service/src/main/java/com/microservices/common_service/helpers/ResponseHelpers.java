//package com.microservices.common_service.helpers;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.microservices.common_service.constants.ResponseConstants;
//import com.microservices.common_service.domain.ResponseModel;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.experimental.UtilityClass;
//import org.springframework.util.ObjectUtils;
//
//import java.time.LocalDateTime;
//import java.util.Date;
//
//@UtilityClass
//public class ResponseHelpers {
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    public String responseData(Object data, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
//        response.setContentType("application/json;charset=UTF-8");
//        ResponseModel responseModel = new ResponseModel();
//        responseModel.setDatetime(LocalDateTime.now().toString());
//        responseModel.setTimestamp(new Date().getTime());
//        responseModel.setPath(request.getRequestURI());
//        if (ObjectUtils.isEmpty(data)) {
//            response.setStatus(ResponseConstants.ResponseStatus.SUCCESS.getValue());
//            responseModel.setCode(ResponseConstants.ResponseStatus.DATA_NOT_FOUND.getValue());
//            responseModel.setStatus(ResponseConstants.ResponseStatus.DATA_NOT_FOUND.getMessage());
//            responseModel.setMessage(ResponseConstants.ResponseMessage.SYSTEM_DATA_NOT_FOUND.getMessage());
//            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseModel);
//        }
//        response.setStatus(ResponseConstants.ResponseStatus.SUCCESS.getValue());
//        responseModel.setCode(ResponseConstants.ResponseStatus.SUCCESS.getValue());
//        responseModel.setStatus(ResponseConstants.ResponseStatus.SUCCESS.getMessage());
//        responseModel.setMessage(ResponseConstants.ResponseMessage.SYSTEM_DATA_FOUND.getMessage());
//        responseModel.setData(data);
//        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseModel);
//    }
//
//    public String responseSuccess(String message, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
//        response.setContentType("application/json;charset=UTF-8");
//        ResponseModel responseModel = new ResponseModel();
//        responseModel.setDatetime(LocalDateTime.now().toString());
//        responseModel.setTimestamp(new Date().getTime());
//        responseModel.setPath(request.getRequestURI());
//        responseModel.setCode(ResponseConstants.ResponseStatus.SUCCESS.getValue());
//        responseModel.setStatus(ResponseConstants.ResponseStatus.SUCCESS.getMessage());
//        responseModel.setMessage(message);
//        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseModel);
//    }
//}
