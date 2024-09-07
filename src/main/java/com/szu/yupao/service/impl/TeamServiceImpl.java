package com.szu.yupao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szu.yupao.common.ErrorCode;
import com.szu.yupao.common.ResultUtils;
import com.szu.yupao.common.TeamStatusCode;
import com.szu.yupao.exception.BusinessException;
import com.szu.yupao.mapper.UserMapper;
import com.szu.yupao.mapper.UserTeamMapper;
import com.szu.yupao.pojo.Team;
import com.szu.yupao.pojo.User;
import com.szu.yupao.pojo.UserTeam;
import com.szu.yupao.pojo.dto.*;
import com.szu.yupao.pojo.vo.TeamUserVo;
import com.szu.yupao.pojo.vo.UserVo;
import com.szu.yupao.service.TeamService;
import com.szu.yupao.mapper.TeamMapper;
import com.szu.yupao.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 许猪配僧
 * @description 针对表【team(队伍)】的数据库操作Service实现
 * @createDate 2024-08-11 20:34:12
 */
@Service
@Slf4j
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserTeamMapper userTeamMapper;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    @Transactional
    public Long addTeam(Team team, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN, "用户未登录");
        }
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建失败，请求参数为空");
        }
        int maxNum = team.getMaxNum();
        if (maxNum >= 5 || maxNum < 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建失败，队伍人数异常");
        }
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() >= 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建失败，名字问题");
        }
        String description = team.getDescription();
        if (StringUtils.isBlank(description) || description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建失败，描述问题");
        }
        Integer statusCode = team.getStatus();
        if (statusCode < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建失败，状态问题");
        }
        TeamStatusCode status = TeamStatusCode.getStatusByValue(statusCode);
        String password = team.getPassword();
        if (TeamStatusCode.SECRET.equals(status) && (StringUtils.isBlank(password) || password.length() > 10)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建失败，密码问题");
        }
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建失败，过期时间问题");
        }
        Long id = loginUser.getId();
        LambdaQueryWrapper<Team> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Team::getUserId, id);
        Long hasTeamNum = teamMapper.selectCount(wrapper);
        if (hasTeamNum > 5) {
            throw new BusinessException(ErrorCode.NO_AUTH, "创建队伍过多");
        }

        team.setUserId(userId);
        Integer insertTeam = teamMapper.insert(team);
//        System.out.println(1/0);
        Long teamId = team.getId();
        if (insertTeam <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "插入队伍表失败");
        }
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());

        Integer insertUserTeam = userTeamMapper.insert(userTeam);
        if (insertUserTeam <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "插入用户队伍关系表失败");
        }
        return teamId;

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long deleteTeam(Long teamId, User loginUser) {
        //1、校验请求参数是否为空
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "删除失败，请求参数为空");
        }
        if (teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "删除失败，请求参数为空");
        }
        //2、校验队伍是否存在
        Team team = teamMapper.selectById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "删除失败，删除队伍不存在");
        }
        //3、校验你是否是队长
        Long id = loginUser.getId();
        if (!id.equals(team.getUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTH, "删除失败，你不是队长");
        }
        //4、删除队伍
        LambdaQueryWrapper<Team> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Team::getId, teamId);
        Integer deleteTeam = teamMapper.delete(wrapper);
        if (deleteTeam <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        //5、删除队伍的加入关联信息
        LambdaQueryWrapper<UserTeam> userTeamWrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Team::getId, teamId);
        Integer deleteUserTeam = userTeamMapper.delete(userTeamWrapper);
        if (deleteUserTeam <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        //返回删除队伍的id
        return teamId;
    }

    @Override
    public Long updateTeam(TeamUpdateDto teamUpdateDto, HttpServletRequest request) {
        if (teamUpdateDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "更改失败，请求参数为空");
        }

        //1、获取当前要更改的队伍及队伍id
        Team team = new Team();
        BeanUtils.copyProperties(teamUpdateDto, team);
        Long teamId = team.getId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "更改失败，请求参数为空");
        }

        //2、查询队伍是否存在
        Team updateTeam = teamMapper.selectById(teamId);
        if (updateTeam == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "更改失败，想更改的队伍不存在");
        }

        //3、只有队长或者管理员才能修改
        User loginUser = userService.getLoginUser(request);
        Long loginUserId = loginUser.getId();
        if (!userService.isAdmin(loginUser) && !loginUserId.equals(updateTeam.getUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTH, "更改失败，无权限");
        }

        //4、队伍状态如果是加密，则一定要有密码
        Integer statusValue = teamUpdateDto.getStatus();
        TeamStatusCode status = TeamStatusCode.getStatusByValue(statusValue);
        if (status.equals(TeamStatusCode.SECRET)) {
            if (StringUtils.isBlank(teamUpdateDto.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密状态下，密码不能为空");
            }
        }

        //4、进行修改
        LambdaQueryWrapper<Team> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Team::getId, teamId);
//        Integer update = teamMapper.update(team, wrapper);

        Integer update = teamMapper.updateById(team);
        if (update <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更改失败");
        }
        return teamId;
    }

    @Override
    public Team getTeamById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<Team> wrapper = new LambdaQueryWrapper();
        wrapper.eq(Team::getId, id);
        Team team = teamMapper.selectOne(wrapper);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return team;
    }

    @Override
    public List<TeamUserVo> getTeamList(TeamQueryDto teamQueryDto, User loginUser) {
        LambdaQueryWrapper<Team> wrapper = new LambdaQueryWrapper<>();
        //队伍id
        Long id = teamQueryDto.getId();

        //组合查询条件
        if (teamQueryDto != null) {
            long pageSize = teamQueryDto.getPageSize();
            long pageNum = teamQueryDto.getPageNum();
            //根据搜索内容在描述和名字模糊查询
            String searchText = teamQueryDto.getSearchText();
            if (StringUtils.isNotBlank(searchText)) {
                wrapper.and(qw -> qw.like(Team::getName, searchText).or().like(Team::getDescription, searchText));
            }
            String name = teamQueryDto.getName();
            if (StringUtils.isNotBlank(name) && name.length() < 20) {
                wrapper.like(Team::getName, name);
            }
            //获取队伍状态值
            Integer value = teamQueryDto.getStatus();
            //根据输入的队伍状态值，获得队伍的状态
            TeamStatusCode status = TeamStatusCode.getStatusByValue(value);
            //保证默认为公开状态
            if (status == null) {
                status = TeamStatusCode.PUBLIC;
            }
            //管理员有资格查看加密的房间
            boolean isAdmin = userService.isAdmin(loginUser);
            if (!isAdmin && status.equals(TeamStatusCode.PRIVATE)) {
                throw new BusinessException(ErrorCode.NO_AUTH, "你不是管理员，不能查看私密的队伍");
            }
            //普通逻辑，输入状态时啥就查啥
            wrapper.eq(Team::getStatus, status.getValue());
            String description = teamQueryDto.getDescription();
            if (StringUtils.isNotBlank(description) && description.length() < 512) {
                wrapper.like(Team::getDescription, description);
            }
            if (id != null && id > 0) {
                wrapper.eq(Team::getId, id);
            }
            Integer maxNum = teamQueryDto.getMaxNum();
            if (maxNum != null && maxNum <= 5 && maxNum > 0) {
                wrapper.eq(Team::getMaxNum, maxNum);
            }
            Long userId = teamQueryDto.getUserId();
            if (userId != null && userId > 0) {
                wrapper.eq(Team::getUserId, userId);
            }
        }
        //不展示过期的队伍
        wrapper.and(queryWrapper -> queryWrapper.gt(Team::getExpireTime, new Date()).or().isNull(Team::getExpireTime));
        List<Team> teamList = teamMapper.selectList(wrapper);
        //允许查询为空的结果，所以不报错
        if (CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<>();
        }
        //关联查询用户信息
        //1. 自己写sql
        //查询队伍和创建人的信息
        //select teamId,userId,name,description,maxNum from team as t inner join user as u on t.userId = u.id
        //查询队伍和已加入队伍的信息
        //select * from team as t inner join user_team as ut on t.id = ut.teamId
        List<TeamUserVo> teamUserVoList = new ArrayList<>();

        //关联查询创建人的用户信息
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if (userId == null) {
                continue;
            }
            //判断当前用户是否在这个队伍里面
            UserInDto userInDto = new UserInDto();
            Long teamId = team.getId();
            userInDto.setTeamId(teamId);
            Boolean inTeam = userService.isInTeam(userInDto, loginUser);

            User user = userMapper.selectById(userId);
            TeamUserVo teamUserVo = new TeamUserVo();
            BeanUtils.copyProperties(team, teamUserVo);
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user, userVo);
            teamUserVo.setCreateUser(userVo);
            teamUserVo.setInTeam(inTeam);
            teamUserVoList.add(teamUserVo);
        }


        return teamUserVoList;
    }

    @Override
    public Long joinTeam(TeamJoinDto teamJoinDto, HttpServletRequest request) {
        //1、校验空值
        if (teamJoinDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "加入失败，请求参数为空");
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN, "加入失败，用户未登录");

        }
        Long userId = loginUser.getId();
        //要加入的那个队伍
        Long teamId = teamJoinDto.getTeamId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "加入失败，请求参数为空");
        }
        Team team = teamMapper.selectById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "加入失败，队伍为空");
        }
        //2、只能加入未满人，已过期的队伍
        Date expireTime = team.getExpireTime();
        if (expireTime != null && team.getExpireTime().before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "加入失败，队伍过期");
        }


        RLock lock = redissonClient.getLock("yupao:join_team");
        try {
            while (true) {
                if (lock.tryLock(0, 30000, TimeUnit.MILLISECONDS)){
                    System.out.println(Thread.currentThread().getId() + "获得lock");
                    //2、当前用户的加入队伍数量满了
                    LambdaQueryWrapper<UserTeam> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(UserTeam::getUserId, userId);
                    Long hasTeamNum = userTeamMapper.selectCount(wrapper);
                    if (hasTeamNum > 5) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "加入失败，加入队伍数量已满");
                    }
                    wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(UserTeam::getTeamId, teamId);
                    Long hasPeopleNum = userTeamMapper.selectCount(wrapper);
                    if (hasPeopleNum >= team.getMaxNum()) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "加入失败，队伍已满人");
                    }
//        LambdaQueryWrapper<Team> teamWrapper = new LambdaQueryWrapper<>();
//        teamWrapper.lt(Team::getExpireTime, expireTime);
//        Team team1 = teamMapper.selectOne(teamWrapper);
//        if (team1 == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "加入失败，队伍已过期");
//        }
                    //3、不能重复加入已加入的队伍
                    //自定义sql，
                    List<Long> joinedTeamIdList = userTeamMapper.selectTeam(userId);
                    if (joinedTeamIdList.contains(teamId)) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "加入失败，不能重复加入");
                    }
                    //4、禁止加入私有房间
                    Integer statusValue = team.getStatus();
                    TeamStatusCode status = TeamStatusCode.getStatusByValue(statusValue);
                    if (status.equals(TeamStatusCode.PRIVATE)) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "加入失败，不能加入私有房间");
                    }
                    //5、加入队伍是加密的必须要密码，且要匹配
                    String password = teamJoinDto.getPassword();
                    if (status.equals(TeamStatusCode.SECRET)) {
                        if (StringUtils.isBlank(password) || !team.getPassword().equals(password)) {
                            throw new BusinessException(ErrorCode.PARAMS_ERROR, "加入失败，密码不匹配");
                        }
                    }
//        //6、不能加入自己的队伍
//        LambdaQueryWrapper<Team> teamWrapper = new LambdaQueryWrapper<>();
//        teamWrapper.eq(Team::getUserId,userId);
//        Long userOwnTeam = teamMapper.selectCount(teamWrapper);
//        if (userOwnTeam <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "加入失败，不能加入自己的队伍");
//        }
                    //7、修改teamUser表
                    wrapper = new LambdaQueryWrapper<>();
                    UserTeam userTeam = new UserTeam();
                    userTeam.setUserId(userId);
                    userTeam.setTeamId(teamId);
                    userTeam.setJoinTime(new Date());
                    userTeam.setIsDelete(0);
                    int insert = userTeamMapper.insert(userTeam);
                    if (insert <= 0) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "加入失败");
                    }
                }


            }

        } catch (InterruptedException e) {
            log.error("error message: " + e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                System.out.println(Thread.currentThread().getId() + "释放lock");


            }
        }
        return teamId;

    }

    @Override
    public Long exitTeam(TeamQuitDto teamQuitDto, HttpServletRequest request) {
        //1、校验参数是否为空
        if (teamQuitDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "退出失败，请求参数为空");
        }
        Long teamId = teamQuitDto.getTeamId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "退出失败，请求参数异常");
        }
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();

        //2、检验队伍是否存在
        LambdaQueryWrapper<Team> teamWrapper = new LambdaQueryWrapper();
        teamWrapper.eq(Team::getId, teamId);
        Team team = teamMapper.selectOne(teamWrapper);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "退出失败，队伍不存在");
        }

        //3、检验是否加入队伍
        LambdaQueryWrapper<UserTeam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTeam::getTeamId, teamId).eq(UserTeam::getUserId, userId);
        UserTeam userTeam = userTeamMapper.selectOne(wrapper);
        if (userTeam == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "退出失败，尚未加入队伍");
        }

        //4、
        //如果队伍只剩一人，直接解散
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTeam::getTeamId, teamId);
        Long hasPeopleNum = userTeamMapper.selectCount(wrapper);
        if (hasPeopleNum == 1) {
            teamWrapper = new LambdaQueryWrapper();
            teamWrapper.eq(Team::getId, teamId);
            Integer deleteTeam = teamMapper.delete(teamWrapper);
            Integer delete = userTeamMapper.delete(wrapper);
            return delete.longValue();
        } else {
            //如果不是队长，
            wrapper = new LambdaQueryWrapper<>();
            if (userId != team.getUserId()) {
                wrapper.eq(UserTeam::getTeamId, teamId).eq(UserTeam::getUserId, userId);
                Integer delete = userTeamMapper.delete(wrapper);
                if (delete <= 0) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败，系统异常");
                }
                return delete.longValue();
            } else {
                //如果是队长，
                //找出第二个加入该队伍的用户id
                wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(UserTeam::getTeamId, teamId);
                wrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamMapper.selectList(wrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() > 2) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "退出失败，系统异常");
                }
                UserTeam secUserTeam = userTeamList.get(1);
                Long nextTeamLeaderId = secUserTeam.getUserId();
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextTeamLeaderId);
                Integer res = teamMapper.updateById(updateTeam);
                if (res == null || res <= 0) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "退出失败，系统异常");
                }
//                UserTeam updateUserTeam = new UserTeam();
//                BeanUtils.copyProperties(secUserTeam,updateUserTeam);
//                updateUserTeam.setUserId(nextTeamLeaderId);
//                Integer res = userTeamMapper.updateById(updateUserTeam);
                //依然还是需要删除当前用户的这条记录
                wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(UserTeam::getTeamId, teamId).eq(UserTeam::getUserId, userId);
                Integer delete = userTeamMapper.delete(wrapper);
                return delete.longValue();
//                Long secUserId = userTeamMapper.selectSecUser();
//                User secUser = userMapper.selectById(secUserId);
//                UserVo userVo = new UserVo();
//                BeanUtils.copyProperties(secUser, userVo);
//                List<Team> teamList = teamMapper.selectList(null);
//
//                Long res = userTeamMapper.exitTeamCaptain();//把第二个的用户id设置成队长id，在Team表里改，然后不用改那个createUser属性
//
//                TeamUserVo teamUserVo = new TeamUserVo();
//                teamUserVo.setCreateUser(userVo);
//                //依然还是需要删除队长的这条记录
//                wrapper = new LambdaQueryWrapper<>();
//                wrapper.eq(UserTeam::getTeamId, teamId).eq(UserTeam::getUserId, userId);
//                Integer delete = userTeamMapper.delete(wrapper);
//                return delete.longValue();

                //队长解散队伍逻辑
                //LambdaQueryWrapper<UserTeam> wrapper = new LambdaQueryWrapper<>();
                //wrapper.eq(UserTeam::getTeamId,teamId).eq(UserTeam::getUserId,userId);
                //Integer delete = userTeamMapper.delete(wrapper);
                //if (delete <= 0) {
                //    throw new BusinessException(ErrorCode.PARAMS_ERROR, "删除失败，系统异常");
                //}
                //return delete.longValue();
            }
        }
//        Integer failValue = -1;
//        return failValue.longValue();
    }

    @Override
    public List<Team> getJoinTeam(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求的id有问题");
        }
        LambdaQueryWrapper<UserTeam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTeam::getUserId, id);
        List<UserTeam> userTeamList = userTeamMapper.selectList(wrapper);
        if (userTeamList == null) {
            return null;
        }
        List<Team> teamList = new ArrayList<>();
        for (UserTeam userTeam : userTeamList) {
            Team team = teamMapper.selectById(userTeam.getTeamId());
            teamList.add(team);
        }
        return teamList;

    }

    @Override
    public List<Team> getCreateTeam(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求的id有问题");
        }
        LambdaQueryWrapper<Team> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Team::getUserId, id);
        wrapper.gt(Team::getExpireTime, new Date());
        List<Team> teamList = teamMapper.selectList(wrapper);
        if (teamList == null) {
            return null;
        }
        return teamList;
    }
}




