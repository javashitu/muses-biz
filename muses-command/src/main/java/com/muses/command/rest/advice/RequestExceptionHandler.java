package com.muses.command.rest.advice;

import com.muses.domain.rest.response.ApiResult;
import com.muses.common.enums.ServerErrorCodeEnums;
import com.muses.common.exception.ServerException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @ClassName RequestExceptionHandler
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/10 15:54
 */
@Slf4j
@RestControllerAdvice
public class RequestExceptionHandler {


    @ExceptionHandler(ServerException.class)
    public ApiResult serverException(HttpServletRequest request, ServerException exception) {
        log.error("server exception under visit url {} , error is ", request.getRequestURI(), exception);

        return ApiResult.builder().code(exception.getServerErrorCodeEnums().getCode()).message(exception.getServerErrorCodeEnums().getMessage()).build();
    }
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult validationBodyException(HttpServletRequest request, MethodArgumentNotValidException exception) {
        log.error("param wrong under visit url {} , error is ", request.getRequestURI(), exception);

        return ApiResult.builder().code(ServerErrorCodeEnums.PARAM_WRONG.getCode()).message(ServerErrorCodeEnums.PARAM_WRONG.getMessage()).build();
    }

    @ExceptionHandler(Exception.class)
    public ApiResult validationBodyException(HttpServletRequest request, Exception exception) {
        log.error("catch exception under visit url {} , will return default failure ", request.getRequestURI(), exception);
        return ApiResult.builder().code(ServerErrorCodeEnums.SERVER_ERROR.getCode()).message(ServerErrorCodeEnums.SERVER_ERROR.getMessage()).build();
    }

}
