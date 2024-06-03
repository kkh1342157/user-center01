package com.szu.usercenter.pojo.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author kkh
 */

@Data
public class UserRegisterRequest implements Serializable {

    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String planetCode;


}
