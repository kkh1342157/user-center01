package com.szu.yupao.service;

import com.szu.yupao.pojo.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.szu.yupao.pojo.User;
import com.szu.yupao.pojo.dto.TeamJoinDto;
import com.szu.yupao.pojo.dto.TeamQueryDto;
import com.szu.yupao.pojo.dto.TeamQuitDto;
import com.szu.yupao.pojo.dto.TeamUpdateDto;
import com.szu.yupao.pojo.vo.TeamUserVo;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author 许猪配僧
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-08-11 20:34:12
*/
public interface TeamService extends IService<Team> {

    Long addTeam(Team team,HttpServletRequest request);


    Long deleteTeam(Long teamId,User loginUser);

    Long updateTeam(TeamUpdateDto teamUpdateDto, HttpServletRequest request);

    Team getTeamById(long id);

    List<TeamUserVo> getTeamList(TeamQueryDto teamQueryVo, User loginUser);

    Long joinTeam(TeamJoinDto teamJoinDto,HttpServletRequest request);

    Long exitTeam(TeamQuitDto teamQuitDto, HttpServletRequest request);

    List<Team> getJoinTeam(long id);

    List<Team> getCreateTeam(long id);
}
