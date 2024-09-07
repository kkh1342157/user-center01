package com.szu.yupao.common;


import lombok.Data;

import java.io.Serializable;


/**
 * 通用返回类
 * 返回正确的结果和错误的结果，由于返回的时候，希望返回的参数可以有很多不同，故正确的方法会比较多，但是错误的方法是只有一个的
 *
 *
 * @param <T>
 * @author kkh
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    private String description;

    public BaseResponse(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = "ok";
        this.description = "";
    }

    public BaseResponse(int code,  String message,String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public BaseResponse(int code, T data) {
        this.code = code;
        this.data = data;
        this.message = "";
        this.description = "";
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage(), errorCode.getDescription());
    }


}
