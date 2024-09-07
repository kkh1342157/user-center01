package com.szu.yupao.config;

import com.szu.yupao.interceptors.LoginInterceptor;
import com.szu.yupao.interceptors.MyInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {


    @Autowired
    LoginInterceptor loginInterceptor;

    @Autowired
    MyInterceptor myInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(myInterceptor).addPathPatterns("/user/*").excludePathPatterns("/user/userLogin");
    }

    //    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
////        registry.addInterceptor(loginInterceptor).addPathPatterns("/user/recommend");
//        registry.addInterceptor(loginInterceptor).addPathPatterns("/team/*");
//    }
}
