package com.szu.yupao.controller;

import com.baomidou.mybatisplus.core.assist.ISqlRunner;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.szu.yupao.common.BaseResponse;
import com.szu.yupao.common.ErrorCode;
import com.szu.yupao.common.ResultUtils;
import com.szu.yupao.exception.BusinessException;
import com.szu.yupao.mapper.UserTeamMapper;
import com.szu.yupao.pojo.Team;
import com.szu.yupao.pojo.User;
import com.szu.yupao.pojo.UserTeam;
import com.szu.yupao.pojo.dto.*;
import com.szu.yupao.pojo.vo.TeamUserVo;
import com.szu.yupao.service.TeamService;
import com.szu.yupao.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.szu.yupao.constant.UserConstant.ADMIN_ROLE;

@RestController
@RequestMapping("team")
@CrossOrigin
@Slf4j
public class TeamController {

    @Autowired
    private TeamService teamService;


    @Autowired
    private UserService userService;

    @Autowired
    private UserTeamMapper userTeamMapper;


    @PostMapping("delete")
    public BaseResponse<Long> deleteTeam(HttpServletRequest request, @RequestBody TeamDeleteDto teamDeleteDto) {
        if (teamDeleteDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "传入参数为空");
        }
        User loginUser = userService.getLoginUser(request);
        Long teamId = teamDeleteDto.getTeamId();
        Long res = teamService.deleteTeam(teamId, loginUser);
        if (res == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "寻求数据为空");
        }
        return ResultUtils.success(res);


    }


    @PostMapping("exit")
    public BaseResponse<Long> exitTeam(@RequestBody TeamQuitDto teamQuitDto, HttpServletRequest request) {

        if (teamQuitDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "退出失败，请求参数为空");
        }
        Long res = teamService.exitTeam(teamQuitDto, request);
        if (res == null || res <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "退出失败，系统异常");
        }
        return ResultUtils.success(res);
    }

    @PostMapping("join")
    //value得和前端传的参数名字一致
    public BaseResponse<Long> joinTeam(@RequestBody TeamJoinDto teamJoinDto, HttpServletRequest request) {
        if (teamJoinDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "加入失败，请求参数为空");
        }
        Long result = teamService.joinTeam(teamJoinDto, request);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "加入失败，没有这个队伍");
        }
        return ResultUtils.success(result);
    }


    @PostMapping("add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddDto teamAddDto, HttpServletRequest request) {
        if (teamAddDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Team team = new Team();
        BeanUtils.copyProperties(teamAddDto, team);
        Long id = teamService.addTeam(team, request);

        return ResultUtils.success(id);
    }




//    @PostMapping("delete")
//    public BaseResponse<Long> deleteTeam(@RequestParam(value = "id") Long teamId) {
//        if (teamId <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        Long res = teamService.deleteTeam(teamId);
//        return ResultUtils.success(res);
//    }

//    @PostMapping("update")
//    public BaseResponse<Long> updateTeam(@RequestBody Team newTeam, HttpServletRequest request) {
//        if (newTeam == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        Long res = teamService.updateTeam(newTeam, request);
//        return ResultUtils.success(res);
//    }

    @PostMapping("update")
    public BaseResponse<Long> updateTeam(@RequestBody TeamUpdateDto teamUpdateDto, HttpServletRequest request) {
        if (teamUpdateDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long res = teamService.updateTeam(teamUpdateDto, request);
        return ResultUtils.success(res);
    }


//    @GetMapping("getJoin")
//    public BaseResponse<List<Team>> getJoinTeam(@RequestParam long id) {
//        if (id <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        List<Team> teamList = teamService.getJoinTeam(id);
//
//        return ResultUtils.success(teamList);
//    }

    @PostMapping("getJoin")
    public BaseResponse<List<TeamUserVo>> getJoinTeam(@RequestBody TeamQueryDto teamQueryDto, HttpServletRequest request) {
        if (teamQueryDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = userService.getLoginUser(request);
        //由于是自己加入的房间，所以无视管理员权限，默认设置为最高权限查询
        loginUser.setUserRole(ADMIN_ROLE);
        teamQueryDto.setUserId(loginUser.getId());
        List<TeamUserVo> list = teamService.getTeamList(teamQueryDto, loginUser);
        return ResultUtils.success(list);
    }


    @GetMapping("getCreate")
    public BaseResponse<List<Team>> getCreateTeam(@RequestParam long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<Team> teamList = teamService.getCreateTeam(id);

        return ResultUtils.success(teamList);
    }

    @GetMapping("getTeam")
    public BaseResponse<Team> getTeamById(@RequestParam long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        System.out.println("我进来了");
        Team team = teamService.getTeamById(id);
        return ResultUtils.success(team);
    }

//    @GetMapping("list")
//    public BaseResponse<List<Team>> getTeamList(@RequestBody TeamQueryDto teamQueryVo) {
//        if (teamQueryVo == null){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        Team team = new Team();
//        BeanUtils.copyProperties(teamQueryVo,team);
//        //会根据传入对象的属性的值去进行过滤查询
//        LambdaQueryWrapper<Team> wrapper = new LambdaQueryWrapper<>(team);
////        List<Team> list = teamService.list(wrapper);
//        List<Team> list = teamService.list(null);
//        return ResultUtils.success(list);
//    }

    @PostMapping("list")
    public BaseResponse<List<TeamUserVo>> getTeamList(@RequestBody TeamQueryDto teamQueryDto, HttpServletRequest request) {
        if (teamQueryDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = userService.getLoginUser(request);

        List<TeamUserVo> list = teamService.getTeamList(teamQueryDto, loginUser);

        LambdaQueryWrapper<UserTeam> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserTeam::getTeamId,teamQueryDto.getId());
        //加入队伍的人数
        Long l = userTeamMapper.selectCount(queryWrapper);

        //想获得队伍的所有用户信息
        //1、你得先获得队伍，那就先获得队伍id吧
        List<Long> teamIdList = list.stream().map(teamUserVo -> teamUserVo.getId()).collect(Collectors.toList());
        System.out.println("队伍id集合" + teamIdList);
        //2、通过队伍id，可以查到所有的用户id，
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserTeam::getTeamId,teamIdList);
        List<UserTeam> userTeamList = userTeamMapper.selectList(queryWrapper);
        System.out.println("队伍集合" + userTeamList);

        Map<Long, List<UserTeam>> teamIdUserTeamList = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        System.out.println("teamIdUserTeamList" + teamIdUserTeamList);


        list.forEach(
                teamUserVo -> teamUserVo.setTeamNum((long)teamIdUserTeamList.get(teamUserVo.getId()).size())
        );

        return ResultUtils.success(list);
    }


    @GetMapping("list/page")
    public BaseResponse<IPage<Team>> getTeamByPage(@RequestBody TeamQueryDto teamQueryVo) {


        if (teamQueryVo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long pageSize = teamQueryVo.getPageSize();
        long pageNum = teamQueryVo.getPageNum();
        String name = teamQueryVo.getName();

        Team team = new Team();
        BeanUtils.copyProperties(teamQueryVo, team);
        IPage<Team> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Team> wrapper = new LambdaQueryWrapper<>(team);
        wrapper.like(Team::getName, name).gt(Team::getExpireTime, new Date());
        IPage<Team> page1 = teamService.page(page, wrapper);
        return ResultUtils.success(page1);
    }
}
