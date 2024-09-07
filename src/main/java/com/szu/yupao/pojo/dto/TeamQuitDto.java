package com.szu.yupao.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TeamQuitDto implements Serializable {
    /**
     * 退出的队伍id
     */
    private Long teamId;

}
