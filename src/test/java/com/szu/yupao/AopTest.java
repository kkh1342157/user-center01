package com.szu.yupao;

import com.szu.yupao.config.AspectTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AopTest {
    @Autowired
    private AspectTest aspectTest;

    @Test
    public void testAop() {
        System.out.println("测试类的testAop启动了");
        aspectTest.test();
    }
}
