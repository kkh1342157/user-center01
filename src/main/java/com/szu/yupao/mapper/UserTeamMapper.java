package com.szu.yupao.mapper;

import com.szu.yupao.pojo.UserTeam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author 许猪配僧
* @description 针对表【user_team(用户队伍关系)】的数据库操作Mapper
* @createDate 2024-08-11 20:33:10
* @Entity com.szu.yupao.pojo.UserTeam
*/
public interface UserTeamMapper extends BaseMapper<UserTeam> {

    List<Long> selectTeam(Long userId);
}




