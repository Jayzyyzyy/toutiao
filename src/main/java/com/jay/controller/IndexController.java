package com.jay.controller;

import com.jay.model.User;
import com.jay.service.ToutiaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 *
 */
//@Controller
public class IndexController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private ToutiaoService toutiaoService;

    @RequestMapping(path = {"/","/index"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody   //将内容或对象作为 HTTP 响应正文返回
    public String index(HttpSession session){
        logger.info("Visit Index");

        return "hello nowCoder, " + session.getAttribute("msg") +
                "<br />Say:" + toutiaoService.say();
    }

    @RequestMapping(value = "/profile/{groupId}/{userId}") //{xxx} 指定为含有某变量的一类值
    @ResponseBody
    public String profile(@PathVariable("groupId") String groupId,  //@PathVariable 获得请求url中的动态参数
                          @PathVariable("userId") int userId,
                          @RequestParam(value = "type",defaultValue = "1") int type,   //
                          @RequestParam(value = "key",defaultValue = "nowCoder")  String key){  //@RequestParam获取提交的参数
        logger.info("Visit profile");
        return String.format("GID{%s},UID{%d},TYPE{%d},KEY{%s}",groupId,userId,type,key);
    }

    @RequestMapping(value = {"/vm"})
    public String news(Model model){  //Model存放后台到前台的数据
        model.addAttribute("value1","vv1");

        List<String> colors = Arrays.asList("RED","GREEN","BLUE");

        Map<String,String> map = new HashMap<String,String>();
        for (int i = 0; i < 4; i++) {
            map.put(String.valueOf(i), String.valueOf(i*i));
        }

        model.addAttribute("colors",colors);
        model.addAttribute("map",map);
        model.addAttribute("user", new User("Jay"));

        return "news";  //不加后缀名.vm
    }

    @RequestMapping(value = {"/request"})
    @ResponseBody
    public String request(HttpServletRequest request, HttpServletResponse response,
                          HttpSession session){
        StringBuilder sb = new StringBuilder();
        Enumeration<String> names = request.getHeaderNames();  //请求头
        while(names.hasMoreElements()){
            String key = names.nextElement();
            String value = request.getHeader(key);
            sb.append(key + ":" + value + "<br>");
        }

        sb.append("<br />");

        for(Cookie c: request.getCookies()){ //Cookies
            sb.append("Cookie:");
            sb.append(c.getName());
            sb.append(":");
            sb.append(c.getValue());
            sb.append("<br />");
        }

        sb.append("<br />");

        sb.append("getMethod:" + request.getMethod() + "<br/>");  //request方法
        sb.append("getPathInfo:" + request.getPathInfo()+ "<br/>");
        sb.append("getRequestURI:" + request.getRequestURI()+ "<br/>");
        sb.append("getQueryString:" + request.getQueryString()+ "<br/>");

        return sb.toString();
    }

    @RequestMapping(value = {"/response"})
    @ResponseBody
    public String response(@CookieValue(value = "nowcoderId", defaultValue = "a") String nowcoderId,  //接收cookie参数，Cookie名为value; a为代替值
                           @RequestParam(value = "key", defaultValue = "key") String key,
                           @RequestParam(value = "value", defaultValue = "value") String value,
                           HttpServletResponse response
                           ){
        response.addCookie(new Cookie(key, value));
        response.addHeader(key, value);  //设置字段

        return "NowCoderId From Cookie:" + nowcoderId;
    }

    @RequestMapping(value = "/redirect/{code}")
    public /*RedirectView*/String redirect(@PathVariable("code") int code, HttpSession session){
        /*RedirectView red = new RedirectView("/",true); //相对于容器，转到首页，重回定视图"/"
        if(code == 301){
            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);  //如果code==301，设置永久转移;默认为302/303,临时转移
        }  //302表示临时转移
        return red;*/

        session.setAttribute("msg", "Jump from redirect.");
        return "redirect:/";  //302跳转
    }

    @RequestMapping("/admin")
    @ResponseBody
    public String admin(@RequestParam(value = "key", required = false) String key){ //required=false 表示非必须
        if("admin".equals(key)){
            return "hello admin";
        }
        throw new IllegalArgumentException("Key 错误!!!");  //自定义异常处理器之后，不会再抛出异常
    }

    @ExceptionHandler()
    @ResponseBody  /*自定义异常处理*/
    public String error(Exception e){
        return "error: " + e.getMessage();
    }

}
