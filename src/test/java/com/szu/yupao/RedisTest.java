package com.szu.yupao;

import com.szu.yupao.pojo.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@SpringBootTest
public class RedisTest {

    @Autowired
    //引入他就可以操作redis
    private RedisTemplate redisTemplate;

//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;

    @Test
    void test() {
        //操作String数据结构
        //增
//        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
//        ValueOperations valueOperations = redisTemplate.opsForValue();
////        stringStringValueOperations.set("name", "luoyifei");
//        valueOperations.set("name", "luoyifei");
//        valueOperations.set("age", 20);
//        valueOperations.set("gender", "女");
//        User user = new User();
//        user.setId(1L);
//        user.setUsername("kkh");
//        valueOperations.set("kkhUser", user);
//
//        //查
//        String name = (String) valueOperations.get("name");
////        String name = (String) stringStringValueOperations.get("name");
//        Assertions.assertTrue(name.equals("luoyifei"));
//        Integer age = (Integer) valueOperations.get("age");
//        Assertions.assertTrue(age.equals(20));
//        String gender = (String) valueOperations.get("gender");
//        Assertions.assertTrue(gender.equals("女"));
//        System.out.println((User)valueOperations.get("kkhUser"));
        redisTemplate.delete("name");
    }

}
