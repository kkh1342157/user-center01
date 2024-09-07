package com.szu.yupao.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
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
     * 用户密码
     */
    @NotBlank
    private String userPassword;

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
     * 用户是否删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 星球编号
     */
    private String planetCode;

    private String  tags;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}