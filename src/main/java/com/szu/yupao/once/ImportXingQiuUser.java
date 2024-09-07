package com.szu.yupao.once;

import com.alibaba.excel.EasyExcel;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 导入用户数据库
 */
public class ImportXingQiuUser {

    public static void main(String[] args) {
        String fileName = "D:\\java idea\\idea_java_project\\yupao-backend\\src\\main\\resources\\testExcel.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        List<YuYanStuTableInfo> totalDataList = EasyExcel.read(fileName).head(YuYanStuTableInfo.class).sheet().doReadSync();
//        for (YuYanStuTableInfo yuYanStuTableInfo :totalDataList) {
//            System.out.println(yuYanStuTableInfo);
//        }
        System.out.println(totalDataList.size());
        //将集合转化成流，然后再处理一下，用特殊的收集
        Map<String, List<YuYanStuTableInfo>> collect = totalDataList.stream().filter(YuYanStuTableInfo -> StringUtils.isNotBlank(YuYanStuTableInfo.getName())).collect(Collectors.groupingBy(YuYanStuTableInfo::getName));
        for (Map.Entry<String, List<YuYanStuTableInfo>> entry :collect.entrySet()) {
            String key = entry.getKey();
            List<YuYanStuTableInfo> value = entry.getValue();
            System.out.println(key + " : " + value);
        }

        System.out.println(collect.keySet().size());
    }
}
