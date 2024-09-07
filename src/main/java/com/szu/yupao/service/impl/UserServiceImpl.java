package com.szu.yupao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.szu.yupao.common.ErrorCode;
import com.szu.yupao.exception.BusinessException;
import com.szu.yupao.mapper.TeamMapper;
import com.szu.yupao.mapper.UserMapper;
import com.szu.yupao.mapper.UserTeamMapper;
import com.szu.yupao.pojo.Team;
import com.szu.yupao.pojo.User;
import com.szu.yupao.pojo.UserTeam;
import com.szu.yupao.pojo.dto.UserInDto;
import com.szu.yupao.pojo.vo.UserVo;
import com.szu.yupao.service.UserService;
import com.szu.yupao.utils.AlgorithmUtils;
import com.szu.yupao.utils.MD5Util;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.szu.yupao.constant.UserConstant.ADMIN_ROLE;
import static com.szu.yupao.constant.UserConstant.USER_LOGIN_STATE;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserTeamMapper userTeamMapper;

    @Autowired
    private TeamMapper teamMapper;


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
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");

        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");

        }
        if (planetCode.length() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号过长");

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
        queryCodeWrapper.eq("planetCode", planetCode);
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
        System.out.println("存储成功");
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
        safetyUser.setTags(originUser.getTags());
        safetyUser.setProfile(originUser.getProfile());

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

    /**
     * 用户根据标签搜索
     *
     * @param tagList 用户要拥有的标签
     * @return
     */
//    @Override
//    public List<User> searchUsersByTags(List<String> tagList) {
//        if (CollectionUtils.isEmpty(tagList)) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        //sql查询
////        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
////        // 拼接 and 查询
////        for (String tag :tagList) {
////            wrapper.like(User::getTags,tag);
////        }
////        List<User> users = userMapper.selectList(wrapper);
////        for (User user :users) {
////            User safetyUser = getSafetyUser(user);
////            user = safetyUser;
////        }
////        return users;
//        //内存查询
//        //1.一次性先查询全部的用户
//        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
//        List<User> users = userMapper.selectList(wrapper);
//        //2.在内存中判断是否包含要求的标签
//        Gson gson = new Gson();
//        //使用流语法糖
//        return users.stream().filter(
//                user -> {
//                    String tagStr = user.getTags();
//                    if (StringUtils.isBlank(tagStr)){
//                        return false;
//                    }
//                    Set<String> tagsSet = gson.fromJson(tagStr, new TypeToken<Set<String>>() {
//                    }.getType());
//                    for (String tagName : tagList) {
//                        if (!tagsSet.contains(tagName)) {
//                            return false;
//                        }
//                    }
//                    return true;
//                }
//        ).map(this::getSafetyUser).collect(Collectors.toList());
//
//
//    }
//
    @Override
    public List<User> searchUsersByTags(List<String> tagList) {
        if (CollectionUtils.isEmpty(tagList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //消除第一次数据库连接的时间影响
        userMapper.selectList(null);
        //sql查询
        //sql测试启动时间
        long startTime = System.currentTimeMillis();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        // 拼接 and 查询
        for (String tag : tagList) {
            wrapper.like(User::getTags, tag);
        }
        List<User> users = userMapper.selectList(wrapper);
        for (User user : users) {
            User safetyUser = getSafetyUser(user);
            user = safetyUser;
        }
        //
        log.info("sql query time: " + (System.currentTimeMillis() - startTime));
        //--------------------------------------


        //内存查询
        //1.一次性先查询全部的用户
        startTime = System.currentTimeMillis();
        users = userMapper.selectList(wrapper);
        //2.在内存中判断是否包含要求的标签
        Gson gson = new Gson();
        //使用流语法糖

//      并行流  users.parallelStream().filter(
        users.stream().filter(
                user -> {
                    String tagStr = user.getTags();
                    if (StringUtils.isBlank(tagStr)) {
                        return false;
                    }
                    Set<String> tagsSet = gson.fromJson(tagStr, new TypeToken<Set<String>>() {
                    }.getType());
                    //效果等同于if,封装了一个可能为空的对象，如果为空的话放入else里面的值
                    Optional.ofNullable(tagsSet).orElse(new HashSet<>());
                    for (String tagName : tagList) {
                        if (!tagsSet.contains(tagName)) {
                            return false;
                        }
                    }
                    return true;
                }
        ).map(this::getSafetyUser).collect(Collectors.toList());
        log.info("memory query time: " + (System.currentTimeMillis() - startTime));
        return users;

    }

    /**
     * @param user      请求发过来的user用户参数, 即修改过后的用户信息
     * @param loginUser 当前登录的用户，即发送请求的用户
     * @return
     */
    @Override
    public int updateUser(User user, User loginUser) {
        // 只有管理员和自己才可以修改

        // 如果是管理员，可以任意修改
        if (isAdmin(loginUser)) {
            Long id = user.getId();
            if (id <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            User oldUser = userMapper.selectById(id);
            if (oldUser == null) {
                throw new BusinessException(ErrorCode.NULL_ERROR);
            }
            int update = userMapper.updateById(user);
            return update;
        }
        // 如果不是管理员，只能修改自己
        Long id = loginUser.getId();
        if (!isAdmin(loginUser) && !id.equals(user.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper();
        wrapper.eq(User::getId, id);

        int update = userMapper.update(user, wrapper);
        return update;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        HttpSession session = request.getSession();
        User loginUser = (User) session.getAttribute(USER_LOGIN_STATE);
        if (loginUser == null) {
            return null;
        }
        return loginUser;
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(USER_LOGIN_STATE);
        if (ADMIN_ROLE != user.getUserRole()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isAdmin(User user) {
        if (user == null) {
            return false;
        }
        if (ADMIN_ROLE != user.getUserRole()) {
            return false;
        }
        return true;
    }

    @Override
    public List<User> matchUsers(long num, User loginUser) {

        if (num <= 0 || num >= 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求数量有问题");
        }
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN, "用户未登录");
        }
        //1、获得当前用户的tagsList
        String tags = loginUser.getTags();
        if (StringUtils.isBlank(tags)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "你没有标签");
        }
        Gson gson = new Gson();
        List<String> loginUserTagsList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        //2、查出所有的用户
//        剔除自己：增加了代码，减少了查询和判断
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(User::getId, loginUser.getId()).isNotNull(User::getTags).select(User::getId, User::getTags);
        List<User> userList = userMapper.selectList(wrapper);

//        //3、用map进行存储
//        SortedMap<Integer, Long> userIdSimilarityMap = new TreeMap<>();
        //3、用List<Pair>存储
        List<Pair<User, Long>> userScorePairList = new ArrayList<>();


        //4、一个个进行匹配
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            String toMatchTags = user.getTags();
            if (StringUtils.isBlank(toMatchTags)) {
                continue;
            }
            List<String> toMatchTagsList = gson.fromJson(toMatchTags, new TypeToken<List<String>>() {
            }.getType());
            if (CollectionUtils.isEmpty(toMatchTagsList)) {
                continue;
            }
            long similarity = AlgorithmUtils.tagsMinDistance(loginUserTagsList, toMatchTagsList);
//            剔除自己 ：我这样是增加了代码和判断，查询还是那么多
//            if (similarity == 0) {
//                continue;
//            }
//            userIdSimilarityMap.put(i, similarity);
            User safetyUser = getSafetyUser(user);
            userScorePairList.add(new Pair<>(safetyUser, similarity));
        }
//        List<User> topNumUserList = userIdSimilarityMap.keySet().stream().map(index -> getSafetyUser(userList.get(index))).limit(num).collect(Collectors.toList());
        List<Pair<User, Long>> topPairList = userScorePairList.stream().sorted(new Comparator<Pair<User, Long>>() {
            @Override
            public int compare(Pair<User, Long> o1, Pair<User, Long> o2) {
                return (int) (o1.getValue() - o2.getValue());
            }
        }).limit(num).collect(Collectors.toList());
        List<User> topNumUserList = topPairList.stream().map(pair -> pair.getKey()).collect(Collectors.toList());
        List<User> collect = topNumUserList.stream().map(user -> getSafetyUser(userMapper.selectById(user.getId()))).collect(Collectors.toList());

        return collect;


    }

    @Override
    public Boolean isInTeam(UserInDto userInDto, User loginUser) {
        if (userInDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long userId = loginUser.getId();
        Long teamId = userInDto.getTeamId();
        //2、检查队伍是否存在，是否过期，是否被删除
        LambdaQueryWrapper<Team> teamWrapper = new LambdaQueryWrapper<>();
        teamWrapper.eq(Team::getId, teamId);
        Team team = teamMapper.selectOne(teamWrapper);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        LambdaQueryWrapper<UserTeam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTeam::getTeamId, teamId).eq(UserTeam::getUserId, userId);
        UserTeam userTeam = userTeamMapper.selectOne(wrapper);
        if (userTeam == null) {
//            throw new BusinessException(ErrorCode.NULL_ERROR,"用户不在队伍里面");
            return false;
        }
        return true;
    }


}
