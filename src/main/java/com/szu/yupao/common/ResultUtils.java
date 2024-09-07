package com.szu.yupao.common;


/**
 * 返回工具类
 * 封装了返回的响应类，这样酒方便一些，通过这个方法去调用
 *
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

        return new BaseResponse<>(0, data, "ok1");
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
