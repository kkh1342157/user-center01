package com.szu.yupao.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TeamAddDto implements Serializable {
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
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date expireTime;

//    /**
//     * 创建人id
//     */
//    private Long userId;

    /**
     * 队伍密码
     */
    private String password;

    /**
     * 队伍状态 0-正常公开，1-私有，2-加密
     */
    private Integer status;

}
