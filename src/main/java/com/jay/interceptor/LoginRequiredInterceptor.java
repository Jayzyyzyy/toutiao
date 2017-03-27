package com.jay.interceptor;

import com.jay.model.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *  用户权限 必须登录才能访问某些资源判断拦截器
 */
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {
    @Autowired
    HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        if(hostHolder.getUser() == null){
            httpServletResponse.sendRedirect("/?pop=1");
            return false; //没权限，不放行，重新发送请求到首页
        }
        return true; //有权限，放行
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        //不做
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        //不做
    }
}
