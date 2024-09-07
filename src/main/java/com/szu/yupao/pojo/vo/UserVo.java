package com.szu.yupao.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import com.szu.yupao.pojo.User;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 脱敏用户类
 */
@Data
public class UserVo implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 用户账号
     */
//    @NotBlank
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 个人简介
     */
    private String profile;

    /**
     * 用户电话
     */
    private String phone;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户状态 0-正常
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 权限 0-普通用户 1-管理员
     */
    private Integer userRole;

    /**
     * 星球编号
     */
    private String planetCode;

    private String tags;


}
