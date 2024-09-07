package com.szu.yupao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szu.yupao.pojo.User;

public interface UserMapper extends BaseMapper<User> {

    public Long userRegist(String username,String userPwd);
}
