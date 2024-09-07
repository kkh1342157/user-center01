package com.szu.yupao.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 队伍和用户信息封装类
 * @author kkh
 */
@Data
public class TeamUserVo {


    /**
     * 已加入队伍人数
     *
     */
    private Long teamNum;

    /**
     * 当前用户是否在队伍
     *
     */
    private boolean isInTeam;

    /**
     * id
     */
    private Long id;

    /**
     * 队伍名字
     */
    private String name;

    /**
     * 队伍描述
     */
    private String description;

    /**
     * 队伍最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 创建人id
     */
    private Long userId;

    /**
     * 队伍密码
     */
    private String password;

    /**
     * 队伍状态 0-正常公开，1-私有，2-加密
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建队伍用户
     */
    private UserVo createUser;
}
