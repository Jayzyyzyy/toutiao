package com.jay.service;

import com.jay.dao.LoginTicketDAO;
import com.jay.dao.UserDAO;
import com.jay.model.LoginTicket;
import com.jay.model.User;
import com.jay.util.ToutiaoUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 *  service层
 */
@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public User getUser(int id){
        return userDAO.selectById(id);
    }

    public Map<String,Object> register(String username, String password){
        Map<String,Object> map = new HashMap<String, Object>();
        if(StringUtils.isBlank(username)){  //StringUtils工具类
            map.put("msgname","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("msgpwd","密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(username);

        if(user != null){
            map.put("msgname","用户名已存在");
            return map;
        }

        //注册用户，加强密码强度
        user = new User();
        user.setName(username);
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));  //密码加强字符串
        user.setPassword(ToutiaoUtils.MD5(password+user.getSalt()));
        userDAO.addUser(user);

        //注册
        String ticket = addLoginTicket(user.getId()); //内存中id会更新，数据库分配
        map.put("ticket",ticket);
        return map;
    }

    //登录
    public Map<String,Object> login(String username, String password){
        Map<String,Object> map = new HashMap<String, Object>();
        if(StringUtils.isBlank(username)){
            map.put("msgname","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("msgpwd","密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(username);

        if(user == null){
            map.put("msgname","用户名不存在");
            return map;
        }

        if(!ToutiaoUtils.MD5(password + user.getSalt()).equals(user.getPassword())){
            map.put("msgpwd","密码不正确");
            return map;
        }

        //登陆
        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }

    //添加ticket到数据库
    private String addLoginTicket(int userId){
        LoginTicket ticket = loginTicketDAO.selectByUserId(userId);
        if(ticket != null){
            String t = ticket.getTicket();
            loginTicketDAO.updateStatus(t, 0);
            return t;
        }

        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 24*3600*1000); //一天有效期
        loginTicket.setExpired(date);
        loginTicket.setStatus(0);  //ticket有效
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        loginTicketDAO.addTicket(loginTicket);
        return loginTicket.getTicket();
    }

    //用户登出
    public void logout(String ticket){
        loginTicketDAO.updateStatus(ticket, 2);
    }

}
