package com.szu.yupao.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.szu.yupao.YuPaoApplication;
import com.szu.yupao.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.StopWatch;

/**
 * 用户服务测试
 */
@SpringBootTest(classes = YuPaoApplication.class)
@ContextConfiguration(classes = YuPaoApplication.class)
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
        Long result = userService.userRegist(userAccount, userPassword, checkPassword, planetCode);
        System.out.println(result);
//        Assertions.assertEquals(-2,result);
//        Assertions.assertTrue(result > 0);
    }

    @Test
    void searchUsersByTags() {
        List<String> tagsList = Arrays.asList("java", "python");


        List<User> users = userService.searchUsersByTags(tagsList);
        System.out.println(users.toString());
    }

    @Test
    void testOneInsertTime() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<User> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            User user = new User();
            user.setUsername("mockUser");
            user.setUserAccount("fakeAccount");
            user.setAvatarUrl("https://pic.code-nav.cn/user_avatar/1601072287388278786/9vqTr3HM-WechatIMG1287.jpeg");
            user.setGender(0);
            user.setProfile("");
            user.setUserPassword("");
            user.setPhone("");
            user.setEmail("");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setIsDelete(0);
            user.setPlanetCode("111111");
            user.setTags("[]");
            list.add(user);
//            userService.save(user);
        }
        userService.saveBatch(list, 100);

        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }


    @Test
    void testFastInsertTime() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<User> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            User user = new User();
            user.setUsername("mockUser");
            user.setUserAccount("fakeAccount");
            user.setAvatarUrl("https://pic.code-nav.cn/user_avatar/1601072287388278786/9vqTr3HM-WechatIMG1287.jpeg");
            user.setGender(0);
            user.setProfile("");
            user.setUserPassword("");
            user.setPhone("");
            user.setEmail("");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setIsDelete(0);
            user.setPlanetCode("111111");
            user.setTags("[]");
            list.add(user);
//            userService.save(user);
        }
        userService.saveBatch(list, 100);

        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }


    @Test
    void testMoreInsertTime() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
//        CompletableFuture<void> future = CompletableFuture.runAsync(()->{
//
//        });
        List<User> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            User user = new User();
            user.setUsername("mockUser");
            user.setUserAccount("fakeAccount");
            user.setAvatarUrl("https://pic.code-nav.cn/user_avatar/1601072287388278786/9vqTr3HM-WechatIMG1287.jpeg");
            user.setGender(0);
            user.setProfile("");
            user.setUserPassword("");
            user.setPhone("");
            user.setEmail("");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setIsDelete(0);
            user.setPlanetCode("111111");
            user.setTags("[]");
            list.add(user);
//            userService.save(user);
        }
        userService.saveBatch(list, 100);

        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}