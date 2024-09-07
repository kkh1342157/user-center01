package com.szu.yupao.service;

import com.szu.yupao.utils.AlgorithmUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * 算法工具类测试
 */
public class AlgorithmUtilsTest {
    @Test
    public void test() {
        String s1 = "鱼皮是狗";
        String s2 = "鱼皮不是狗";
        String s3 = "鱼皮是鱼不是狗";
        int score1 = AlgorithmUtils.minDistance(s1, s2);
        int score2 = AlgorithmUtils.minDistance(s1, s3);
        System.out.println(score1);
        System.out.println(score2);
    }

    @Test
    public void testTags() {
        List<String> list1 = Arrays.asList("Java", "大一", "男");
        List<String> list2 = Arrays.asList("Java", "大二", "女");
        List<String> list3 = Arrays.asList("Python", "大二", "女");
        int score1 = AlgorithmUtils.tagsMinDistance(list1, list2);
        int score2 = AlgorithmUtils.tagsMinDistance(list1, list3);
        System.out.println(score1);
        System.out.println(score2);
    }
}