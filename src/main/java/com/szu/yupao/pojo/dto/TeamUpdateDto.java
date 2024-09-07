package com.szu.yupao.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TeamUpdateDto implements Serializable {
    /**
     * 队伍名字
     */
    private String name;

    /**
     * 队伍描述
     */
    private String description;

    /**
     * 队伍id
     */
    private Long id;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 队伍密码
     */
    private String password;

    /**
     * 队伍状态 0-正常公开，1-私有，2-加密
     */
    private Integer status;

}
