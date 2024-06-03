package com.szu.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szu.usercenter.common.ErrorCode;
import com.szu.usercenter.exception.BusinessException;
import com.szu.usercenter.mapper.UserMapper;
import com.szu.usercenter.pojo.User;
import com.szu.usercenter.service.UserService;
import com.szu.usercenter.utils.MD5Util;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.szu.usercenter.constant.UserConstant.USER_LOGIN_STATE;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;


//    @Override
//    public Long userRegist(String username,String userPwd) {
//        HashMap<String , Object> objectObjectHashMap = new HashMap<>();
//        objectObjectHashMap.put("userAccount",username);
//        if (username.length() >= 4 && userMapper.selectByMap(objectObjectHashMap) == null){
//            if (userPwd.length() >= 8 && ){
//
//            }
//        }
//        Long l = userMapper.userRegist(username,userPwd);
//        User user = new User();
//        user.setUserAccount(username);
//        user.setUserPassword(userPwd);
//        int insert = userMapper.insert(user);
//        User user1 = userMapper.selectById(insert);
//        return user1.getId();
//    }

    @Override
    public Long userRegist(String userAccount, String userPassword, String checkPassword, String planetCode) {
        //1：校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号过短");

        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");

        }
        if (planetCode.length() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号过长");

        }
//        //账户不能重复
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("userAccount",userAccount);
//        long count = this.count(queryWrapper);
//        if (count > 0){
//            return (long) -1;
//        }
        //账户不能包含特殊字符
        String regex = "^[a-zA-Z0-9]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(userAccount);
        if (!matcher.find()) {
            return (long) -1;
        }
        //密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            return (long) -1;
        }
        //账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.count(queryWrapper);
        if (count > 0) {
            return (long) -1;
        }
        //编号不能重复
        QueryWrapper<User> queryCodeWrapper = new QueryWrapper<>();
        queryCodeWrapper.eq("planetCode",planetCode);
        long countCode = this.count(queryCodeWrapper);
        if (countCode > 0) {
            return (long) -2;
        }

        //2：对密码进行加密
        String encrypt = MD5Util.encrypt(userPassword);
        //3: 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encrypt);
        user.setPlanetCode(planetCode);
//        int insert = userMapper.insert(user);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            return (long) -1;
        }
        return user.getId();
    }

    @Override
    public User userLoign(String userAccount, String userPassword, HttpServletRequest request) {
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        if (userAccount.length() < 4) {
            return null;
        }
        if (userPassword.length() < 8) {
            return null;
        }
        //账户不能包含特殊字符
        String regex = "^[a-zA-Z0-9]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(userAccount);
        if (!matcher.find()) {
            return null;
        }
        //2：对密码进行加密
        String encrypt = MD5Util.encrypt(userPassword);
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userPassword", encrypt)
                .eq("userAccount", userAccount);
        User user = this.getOne(userQueryWrapper);
        //用户不存在
        if (user == null) {
            log.info("user login failed , userAccount cannot match userPassword");
            return null;
        }
        //4：用户脱敏
        User safetyUser = getSafetyUser(user);
        //3：记录用户的登录态
        HttpSession session = request.getSession();
        session.setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }


    /**
     * 用户脱敏
     *
     * @param originUser
     * @return 脱敏后的账户
     */
    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();

        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setPlanetCode(originUser.getPlanetCode());

        return safetyUser;
    }

    /**
     * 用户注销
     *
     * @return
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }


    @Override
    public boolean deleteUser(long id) {
        if (id <= 0) {
            return false;
        }
        boolean b = this.removeById(id);
        return b;
    }

    @Override
    public List<User> searchUsers(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNoneBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> users = this.list(queryWrapper);
        return users.stream().map(user -> {
            user.setUserPassword(null);
            return user;
        }).collect(Collectors.toList());
//        return users;
    }


}
