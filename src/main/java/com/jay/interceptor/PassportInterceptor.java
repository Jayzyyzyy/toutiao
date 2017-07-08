package com.jay.interceptor;

import com.jay.dao.LoginTicketDAO;
import com.jay.dao.UserDAO;
import com.jay.model.HostHolder;
import com.jay.model.LoginTicket;
import com.jay.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 *  用户登录状态验证  检测拦截器
 */
@Component
public class PassportInterceptor implements HandlerInterceptor {
    @Autowired
    private LoginTicketDAO loginTicketDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String ticket = null;
        if(httpServletRequest.getCookies() != null){
            for(Cookie cookie : httpServletRequest.getCookies()){
                if(cookie.getName().equals("ticket")){
                    ticket = cookie.getValue();
                    break;
                }
            }
        }

        if(ticket != null){
            LoginTicket loginTicket = loginTicketDAO.selectByTicket(ticket); //存在ticket，在数据库中查找是否存在该对象
            if(loginTicket == null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus() != 0){ //用户登录信息已失效
                return true;
            }
            //该用户已经登陆且有效,将用户信息放到上下文中
            User user = userDAO.selectById(loginTicket.getUserId());
            hostHolder.set(user);
        }

        return true;
    }

    //渲染视图之前处理
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        if(modelAndView != null && hostHolder.getUser() != null){
            modelAndView.addObject("user", hostHolder.getUser()); //MV加入user，模板渲染用
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        hostHolder.clear(); //视图渲染之后执行，清空用户登录状态
    }
}
