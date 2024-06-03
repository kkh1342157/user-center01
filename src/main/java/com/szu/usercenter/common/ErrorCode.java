package com.szu.usercenter.common;

import lombok.Data;

/**
 * 错误码
 *
 * @author kkh
 */
public enum ErrorCode {
    SUCCESS(0,"ok",""),
    PARAMS_ERROR(40000,"请求参数错误",""),

    NULL_ERROR(40001,"请求数据为空",""),

    NO_LOGIN(40100,"未登录",""),
    NO_AUTH(40101,"无权限",""),
   SYSTEM_ERROR(50000,"系统异常","");

    private int code;

    /**
     * 状态码信息
     *
     */
    private String message;
    /**
     * 为什么错误，具体错的原因
     *
     */
    private String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
