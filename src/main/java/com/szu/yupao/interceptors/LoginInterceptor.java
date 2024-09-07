package com.szu.yupao.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.szu.yupao.common.BaseResponse;
import com.szu.yupao.common.ErrorCode;
import com.szu.yupao.common.ResultUtils;
import com.szu.yupao.pojo.User;
import com.szu.yupao.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User loginUser = userService.getLoginUser(request);
        System.out.println("进入拦截器啦");
        //拦截器取到请求先进行判断，如果是OPTIONS请求，则放行
        if ("OPTIONS".equals(request.getMethod().toUpperCase())) {
            System.out.println("Method:OPTIONS");
            return true;
        }

        if (loginUser == null) {
            System.out.println("未登录，被拦截啦");
            BaseResponse<Object> result = ResultUtils.error(ErrorCode.NO_LOGIN);
            ObjectMapper objectMapper = new ObjectMapper();
            String s = objectMapper.writeValueAsString(result);
            response.getWriter().print(s);
            return false;
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);

    }


}
