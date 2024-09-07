package com.szu.yupao;

import com.szu.yupao.mapper.UserMapper;
import com.szu.yupao.pojo.User;
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
