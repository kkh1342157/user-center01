package com.szu.usercenter.controller;

import com.szu.usercenter.common.BaseResponse;
import com.szu.usercenter.common.ErrorCode;
import com.szu.usercenter.common.ResultUtils;
import com.szu.usercenter.exception.BusinessException;
import com.szu.usercenter.pojo.User;
import com.szu.usercenter.pojo.request.UserLoginRequest;
import com.szu.usercenter.pojo.request.UserRegisterRequest;
import com.szu.usercenter.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.szu.usercenter.constant.UserConstant.ADMIN_ROLE;
import static com.szu.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 *
 * @author kkh
 */

@RestController
@RequestMapping("user")
//@CrossOrigin(origins = "http://62.234.35.31",allowCredentials = "true")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

//    private HttpServletRequest httpServletRequest;

    @PostMapping("userLogin")
    public BaseResponse<User> userLoign(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return ResultUtils.error(ErrorCode.NULL_ERROR);
        }
        User user = userService.userLoign(userAccount, userPassword, request);
        if (user == null) {
            return ResultUtils.error(ErrorCode.NO_PWD);
//            return null;
        }
//        return new BaseResponse<>(0,user,"ok");
        return ResultUtils.success(user);
    }

    @PostMapping("userRegist")
    public BaseResponse<Long> userRegist(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            return null;
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword,planetCode)) {
            return null;
        }

        long id = userService.userRegist(userAccount, userPassword, checkPassword,planetCode);
//        return new BaseResponse<>(0,id,"ok");
        return ResultUtils.success(id);

    }

    @PostMapping("delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id,HttpServletRequest request) {
        if (!isAdmin(request)){
            return null;
        }
        if (id <= 0){
            return null;
        }

        boolean delete = userService.deleteUser(id);
        return ResultUtils.success(delete);
    }

    @GetMapping("search")
    public BaseResponse<List<User>> searchUsers(@Param("username") String username,HttpServletRequest request) {

        if (!isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        List<User> users = userService.searchUsers(username);
        return ResultUtils.success(users);
    }

    @GetMapping("current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        HttpSession session = request.getSession();
        System.out.println(session.toString());
        User currentUser =  (User)session.getAttribute(USER_LOGIN_STATE);
        if (currentUser == null){
            return ResultUtils.error(ErrorCode.NO_LOGIN);
        }
        long userId = currentUser.getId();
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }


    /**
     * 是否为管理员
     *
     *
     * @param request
     * @return
     */
    private boolean isAdmin(HttpServletRequest request) {
        //仅管理员可查询
        User user = (User)request.getSession().getAttribute(USER_LOGIN_STATE);

        if (user == null){
            return false;
        }

        if (user.getUserRole() != ADMIN_ROLE){
            return false;
        }
        return true;
    }

    @PostMapping("logout")
    private BaseResponse<Integer> userLogout(HttpServletRequest request){
        if (request == null){
            return null;
        }
        int i = userService.userLogout(request);
        return ResultUtils.success(i);
    }
}
