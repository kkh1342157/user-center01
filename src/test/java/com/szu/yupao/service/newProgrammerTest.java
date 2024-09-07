package com.szu.yupao.service;


import com.szu.yupao.pojo.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class newProgrammerTest {

    @Test
    public void test() {
        List<Double> doubleList = Arrays.asList(1.1,2.2,3.3,4.4,5.5,6.6,7.7,8.8);
        List<Integer> list = Arrays.asList(1,2,3,4,5,6,7);
        List<Double> collect = list.stream().map(index -> doubleList.get(index)).collect(Collectors.toList());
        System.out.println("collect = " + collect);

    }

}
