package com.szu.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szu.usercenter.pojo.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface UserService extends IService<User>{


    /**
     * 用户注册
     *
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkPassword 用户校验码
     * @return 新用户id
     */
    Long userRegist(String userAccount,String userPassword,String checkPassword,String planetCode);


    /**
     * 用户登录
     *
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @return 脱敏后的用户信息
     */
    User userLoign(String userAccount, String userPassword, HttpServletRequest request);

    boolean deleteUser(long id);


    List<User> searchUsers(String username);

    /**
     * 用户脱敏
     *
     * @param user
     * @return
     */
    User getSafetyUser(User user);


    /**
     * 用户注销
     *
     * @return
     */
    int userLogout(HttpServletRequest request);
}
