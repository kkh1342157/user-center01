package com.szu.yupao.once;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.util.ListUtils;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * 导入excel
 */
public class ImportExcel {

    /**
     * 最简单的读
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link YuYanStuTableInfo}
     * <p>
     * 2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器，参照{@link YuYanStuTableInfoListener}
     * <p>
     * 3. 直接读即可
     */


    /**
     * 监听器读
     *
     */
    @Test
    public void simpleRead() {
        // 写法1：JDK8+ ,不用额外写一个YuYanStuTableInfoListener
        // since: 3.0.0-beta1
        String fileName = "D:\\java idea\\idea_java_project\\yupao-backend\\src\\main\\resources\\testExcel.xlsx";
        // 这里默认每次会读取100条数据 然后返回过来 直接调用使用数据就行
        EasyExcel.read(fileName, YuYanStuTableInfo.class, new YuYanStuTableInfoListener()).sheet().doRead();

    }

    /**
     * 同步读
     *
     */
    @Test
    public void synRead() {
        // 写法2：
        // 匿名内部类 不用额外写一个YuYanStuTableInfoListener
        String fileName = "D:\\java idea\\idea_java_project\\yupao-backend\\src\\main\\resources\\testExcel.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        List<YuYanStuTableInfo> totalDataList = EasyExcel.read(fileName).head(YuYanStuTableInfo.class).sheet().doReadSync();
        for (YuYanStuTableInfo yuYanStuTableInfo :totalDataList) {
            System.out.println(yuYanStuTableInfo);
        }


    }
}
