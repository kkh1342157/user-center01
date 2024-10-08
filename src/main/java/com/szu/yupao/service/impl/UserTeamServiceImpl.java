package com.szu.yupao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szu.yupao.pojo.UserTeam;
import com.szu.yupao.service.UserTeamService;
import com.szu.yupao.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author 许猪配僧
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2024-08-11 20:33:10
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




