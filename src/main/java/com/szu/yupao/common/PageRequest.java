package com.szu.yupao.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PageRequest {
    /**
     * 页面大小
     */
    private long pageSize;

    /**
     * 当前是第几页
     */
    @JsonProperty("PageNum")
    private long pageNum;
}
