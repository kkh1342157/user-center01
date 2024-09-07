package com.szu.yupao;

import com.szu.yupao.config.RedissonConfig;
import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class RedissonTest {

    @Autowired
    private RedissonClient redissonClient;

    @Test
    void test(){
        // list
        List<String > list = new ArrayList<>();
        list.add("yupi");
        System.out.println("list" +list.get(0) );
//        list.remove(0);

        RList<String> rList = redissonClient.getList("test-list");
        System.out.println("rList" +rList.get(0) );
        rList.remove(0);

        // map
        Map<String ,Integer > map = new HashMap<>();
        map.put("yupi",10);
        System.out.println(map.get("yupi"));

        RMap<Object, Object> rMap = redissonClient.getMap("test-map");
        rMap.put("rMap",10);
        System.out.println(rMap.get("rMap"));
        // set

        // stack
    }

}
