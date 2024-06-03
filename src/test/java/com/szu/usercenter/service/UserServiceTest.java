package com.szu.usercenter.service;

import java.util.Date;

import com.szu.usercenter.UserCenterApplication;
import com.szu.usercenter.pojo.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务测试
 */
@SpringBootTest(classes = UserCenterApplication.class)
@ContextConfiguration(classes = UserCenterApplication.class)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void test_01() {
        User user = new User();
        user.setUsername("luoxiaofei");
        user.setUserAccount("lxf");
        user.setAvatarUrl("https://pic.code-nav.cn/user_avatar/1601072287388278786/9vqTr3HM-WechatIMG1287.jpeg");
        user.setGender(0);
        user.setUserPassword("123456");
        user.setPhone("123");
        user.setEmail("123");
        user.setUserStatus(0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setIsDelete(0);

        userService.save(user);
    }

    @Test
    void userRegist() {
        String userAccount = "xqhkkh";
        String userPassword = "123456789";
        String checkPassword = "123456789";
        String planetCode = "1234";
        Long result = userService.userRegist(userAccount, userPassword, checkPassword,planetCode);
        System.out.println(result);
//        Assertions.assertEquals(-2,result);
//        Assertions.assertTrue(result > 0);
    }
}