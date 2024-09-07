package com.szu.yupao.exception;

import com.szu.yupao.common.BaseResponse;
import com.szu.yupao.common.ErrorCode;
import com.szu.yupao.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 全局异常处理器
 *
 * @author kkh
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //针对什么异常要做什么处理
    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandler(BusinessException e) {
//        log.error("businessException: " + e.getMessage() + "," + e.getDescription(), e);
        log.error("businessException: " + e.getMessage(), e);

        return ResultUtils.error(e.getCode(),e.getMessage(), e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(RuntimeException e) {
        log.error("runtimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage(), "");
    }
}
