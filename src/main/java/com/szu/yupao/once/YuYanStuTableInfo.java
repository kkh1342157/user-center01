package com.szu.yupao.once;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class YuYanStuTableInfo {

    @ExcelProperty("考生号")
    String kaoShengHao;

    @ExcelProperty("姓名")

    String name;
    @ExcelProperty("准考证号")

    String id;

    @ExcelProperty("选科组合")
    String xuankezuhe;
}
