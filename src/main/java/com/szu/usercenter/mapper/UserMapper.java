package com.szu.usercenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szu.usercenter.pojo.User;

public interface UserMapper extends BaseMapper<User> {

    public Long userRegist(String username,String userPwd);
}
