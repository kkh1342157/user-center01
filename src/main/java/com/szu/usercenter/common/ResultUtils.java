package com.szu.usercenter.common;


/**
 * 返回工具类
 *
 * @author kkh
 */
public class ResultUtils {

    /**
     * 成功
     * @param data
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 失败
     * @param errorCode
     * @return
     * @param <T>
     */
//    public static <T> BaseResponse<T> error(ErrorCode errorCode) {
//        return new BaseResponse<>(errorCode.getCode(), null, errorCode.getMessage(), errorCode.getDescription());
//    }
    public static <T> BaseResponse<T> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    public static <T> BaseResponse<T> error(ErrorCode errorCode,String message,String description) {
        return new BaseResponse<>(errorCode.getCode(),message,description);
    }

    public static <T> BaseResponse<T> error(int code,String message,String description) {
        return new BaseResponse<>(code,message,description);
    }
}
