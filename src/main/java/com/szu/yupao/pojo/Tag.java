package com.szu.yupao.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 标签
 * @TableName tag
 */
@Data
public class Tag implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标签名称
     */
    private String tagname;

    /**
     * 用户id
     */
    private Long userid;

    /**
     * 父标签id
     */
    private Long parentid;

    /**
     * 创建时间
     */
    private Date createtime;

    /**
     * 更新时间
     */
    private Date updatetime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isdelete;

    /**
     * 是否是父标签 0--不是 1--是
     */
    private Integer isparent;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}