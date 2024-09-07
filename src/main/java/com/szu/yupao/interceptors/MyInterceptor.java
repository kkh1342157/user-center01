package com.szu.yupao.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.szu.yupao.common.BaseResponse;
import com.szu.yupao.common.ErrorCode;
import com.szu.yupao.common.ResultUtils;
import com.szu.yupao.exception.BusinessException;
import com.szu.yupao.pojo.User;
import com.szu.yupao.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MyInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User loginUser = userService.getLoginUser(request);
        System.out.println("进入了我自定义的拦截器");
        if ("OPTIONS".equals(request.getMethod().toUpperCase())) {
            System.out.println("Method:OPTIONS");
            return true;
        }
        if (loginUser == null) {
            System.out.println("未登录，被拦截啦");
            ObjectMapper objectMapper = new ObjectMapper();
            BaseResponse<Object> error = ResultUtils.error(ErrorCode.NO_LOGIN,"1234","1234");
            String s = objectMapper.writeValueAsString(error);
            response.getWriter().print(s);
            //响应写回去的只能是字符串，而我生成的响应对象result是json字符串
            return false;
        }

        return HandlerInterceptor.super.preHandle(request, response, handler);

    }


}
