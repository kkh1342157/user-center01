package com.szu.usercenter;

import com.szu.usercenter.mapper.UserMapper;
import com.szu.usercenter.pojo.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = YuPaoApplication.class)
@SpringBootTest(classes = YuPaoApplication.class)
class YuPaoApplicationTests {

    @Resource
    private UserMapper userMapper;

    @Test
    void contextLoads() {
        User user = userMapper.selectById(1L);
        System.out.println(user);
    }

}
