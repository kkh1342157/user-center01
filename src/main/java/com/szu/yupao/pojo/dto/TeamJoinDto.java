package com.szu.yupao.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TeamJoinDto implements Serializable {

    /**
     * 加入的队伍id
     */
    private Long teamId;

    /**
     * 加入加密队伍时需要输入队伍密码
     */
    private String password;

}
