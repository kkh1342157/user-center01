package com.szu.yupao.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.szu.yupao.common.BaseResponse;
import com.szu.yupao.common.ErrorCode;
import com.szu.yupao.common.ResultUtils;
import com.szu.yupao.exception.BusinessException;
import com.szu.yupao.pojo.User;
import com.szu.yupao.pojo.dto.UserInDto;
import com.szu.yupao.pojo.request.UserLoginRequest;
import com.szu.yupao.pojo.request.UserRegisterRequest;
import com.szu.yupao.pojo.vo.UserVo;
import com.szu.yupao.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.szu.yupao.constant.UserConstant.ADMIN_ROLE;
import static com.szu.yupao.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 *
 * @author kkh
 */

@RestController
@RequestMapping("user")
//@CrossOrigin(origins = "http://62.234.35.31",allowCredentials = "true")
@CrossOrigin
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

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
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            return null;
        }

        long id = userService.userRegist(userAccount, userPassword, checkPassword, planetCode);
//        return new BaseResponse<>(0,id,"ok");
        return ResultUtils.success(id);

    }

    @PostMapping("delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return null;
        }
        if (id <= 0) {
            return null;
        }

        boolean delete = userService.deleteUser(id);
        return ResultUtils.success(delete);
    }

    @GetMapping("search")
    public BaseResponse<List<User>> searchUsers(@Param("username") String username, HttpServletRequest request) {

        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        List<User> users = userService.searchUsers(username);
        return ResultUtils.success(users);
    }

    @GetMapping("current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        System.out.println(session.toString());
        User currentUser = (User) session.getAttribute(USER_LOGIN_STATE);
        if (currentUser == null) {
            return ResultUtils.error(ErrorCode.NO_LOGIN);
        }
        long userId = currentUser.getId();
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    @PostMapping("isInTeam")
    public BaseResponse<Boolean> isInTeam(HttpServletRequest request,@RequestBody UserInDto userInDto) {
        if (userInDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN,"用户未登录");
        }
        Boolean isInTeam = userService.isInTeam(userInDto,loginUser);
        if (isInTeam == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"查找不到");
        }
        return ResultUtils.success(isInTeam);
    }



    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    private boolean isAdmin(HttpServletRequest request) {
        //仅管理员可查询
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);

        if (user == null) {
            return false;
        }

        if (user.getUserRole() != ADMIN_ROLE) {
            return false;
        }
        return true;
    }

    @PostMapping("logout")
    private BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        int i = userService.userLogout(request);
        return ResultUtils.success(i);
    }

    @GetMapping("search/tags")
    public BaseResponse<List<User>> searchUsersByTags(@RequestParam(required = false) List<String> tagList) {

        if (CollectionUtils.isEmpty(tagList)) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        List<User> users = userService.searchUsersByTags(tagList);
        return ResultUtils.success(users);
    }

    /**
     * 修改用户信息
     *
     * @param newUser 修改之后的内容
     * @return 返回修改好的对象
     */
    @PostMapping("update")
    public BaseResponse<Integer> updateUser(@RequestBody User newUser, HttpServletRequest request) {
        if (newUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        int i = userService.updateUser(newUser, loginUser);
        return ResultUtils.success(i);
    }


    @GetMapping("myRecommendUsers")
    public BaseResponse<List<User>> myRecommendUsers(int pageSize, int pageNum) {
        if (pageSize <= 0 || pageSize <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<User> page = new Page<>(pageNum, pageSize);
        Page<User> page1 = userService.page(page);
        if (page1 == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        List<User> records = page1.getRecords();
        return ResultUtils.success(records);

    }


    /**
     * @param pageSize 每页多少条
     * @param pageNum  要查询的页码，从哪一页开始
     * @param request
     * @return
     */
    @GetMapping("recommend")
    public BaseResponse<List<User>> recommendUsers(Integer pageSize, Integer pageNum, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        String redisKey = String.format("yupao:user:recommend:%s", userId);
        Page<User> redisPage = (Page<User>) redisTemplate.opsForValue().get(redisKey);
        //如果有缓存的话，就直接返回缓存
        if (redisPage != null) {
            List<User> redisUserList = redisPage.getRecords();
            return ResultUtils.success(redisUserList);
        }
        //没缓存的话，就去数据库查，并且将查到的数据放到缓存redis里
        Page<User> page = userService.page(new Page<>(pageNum, pageSize));
        try {
            redisTemplate.opsForValue().set(redisKey, page, 10000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("redis key error ", e);
        }
        List<User> userList = page.getRecords();

//        List<User> userList = userService.recommendUsers();
//        List<User> userList = userService.list();
//        userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(userList);
    }


    @GetMapping("match")
    public BaseResponse<List<User>> matchUsers(long num, HttpServletRequest request) {
        if (num <= 0 || num >= 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "数量有误");
        }
        User loginUser = userService.getLoginUser(request);
        List<User> userList = userService.matchUsers(num,loginUser);
        if (userList == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"没有匹配");
        }
        return ResultUtils.success(userList);

    }
}
