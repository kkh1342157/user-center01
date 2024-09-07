package com.szu.yupao.pojo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.szu.yupao.common.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TeamQueryDto extends PageRequest implements Serializable {


    /**
     * id
     */
    @JsonProperty("id")
    private Long id;

    /**
     * 队伍名字
     */
    @JsonProperty("name")
    private String name;

    /**
     * 队伍描述
     */
    @JsonProperty("description")
    private String description;

    /**
     * 队伍最大人数
     */

    @JsonProperty("maxNum")
    private Integer maxNum;

    /**
     * 过期时间
     */
    @JsonProperty("expireTime")
    private Date expireTime;

    /**
     * 创建人id
     */
    @JsonProperty("userId")
    private Long userId;


    /**
     * 队伍状态 0-正常公开，1-私有，2-加密
     */
    @JsonProperty("status")
    private Integer status;

    /**
     * 搜索内容
     */
    private String searchText;
}
