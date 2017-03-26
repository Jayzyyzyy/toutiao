package com.jay.controller;

import com.jay.service.UserService;
import com.jay.util.ToutiaoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 *  登录 注册控制器
 */
@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    //注册
    @RequestMapping(path = {"/reg/"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String reg(Model model, @RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam(value = "rember",defaultValue = "0") int rememberme,
                           HttpServletResponse response){
        try{
            Map<String, Object> map = userService.register(username, password);

            if(map.containsKey("ticket")){
                Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
                cookie.setPath("/");//设置全站有效
                if(rememberme > 0) {
                    cookie.setMaxAge(3600 * 24 * 5); // s单位，5天有效
                }
                response.addCookie(cookie);
                return ToutiaoUtils.getJsonString(0, "注册成功");
            }else {
                return ToutiaoUtils.getJsonString(1, map);
            }
        }catch (Exception e){
            logger.error("注册异常: "+e.getMessage());
            return ToutiaoUtils.getJsonString(1, "注册异常");
        }
    }

    //登录
    @RequestMapping(value = {"/login/"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String login(Model model, @RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam(value = "rember",defaultValue = "0") int rememberme, //rememberme表示是否记住用户名/密码，默认不记住，游览器关闭失效
                      HttpServletResponse response){
        try{
            Map<String, Object> map = userService.login(username, password);

            if(map.containsKey("ticket")){
                Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
                cookie.setPath("/");//设置全站有效
                if(rememberme > 0) {
                    cookie.setMaxAge(3600 * 24 * 5); // s单位，5天有效
                }
                response.addCookie(cookie);
                return ToutiaoUtils.getJsonString(0, "登录成功");
            }else {
                return ToutiaoUtils.getJsonString(1, map);
            }
        }catch (Exception e){
            logger.error("登陆异常: "+e.getMessage());
            return ToutiaoUtils.getJsonString(1, "登录异常");
        }
    }


}
