package com.szu.yupao.config;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AspectConfig {

    @Before("execution(public void com.szu.yupao.config.AspectTest.test(..))")
    public void doBefore(){
        System.out.println("doBefore执行了");
    }
}
