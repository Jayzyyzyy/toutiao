package com.jay.controller;

import com.jay.model.EntityType;
import com.jay.model.HostHolder;
import com.jay.model.News;
import com.jay.model.ViewObject;
import com.jay.service.LikeService;
import com.jay.service.NewsService;
import com.jay.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页控制器
 */
@Controller
public class HomeController {
    @Autowired
    UserService userService;

    @Autowired
    NewsService newsService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    private List<ViewObject> getNews(int userId, int offset, int limit){
        List<News> newsList = newsService.getLatestNews(userId, offset, limit);
        int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0 ;

        List<ViewObject> vos = new ArrayList<ViewObject>();
        for (News news : newsList) {
            ViewObject vo = new ViewObject();
            vo.set("news", news);
            vo.set("user", userService.getUser(news.getUserId())); //获取用户信息
            if(localUserId != 0){ //得到用户的喜欢与不喜欢状态
                vo.set("like", likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS,news.getId()));
            }else {
                vo.set("like", 0);
            }
            vos.add(vo);
        }
        return vos;
    }

    //访问首页，看到资讯
    @RequestMapping(value = {"/","/index"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model, @RequestParam(value = "pop",defaultValue = "0") int pop){
        model.addAttribute("vos",getNews(0, 0 ,10));
        model.addAttribute("pop", pop);
        return "home";
    }

    //访问用户，看到资讯（与用户相关）
    @RequestMapping(value = {"/user/{userId}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userIndex(Model model, @PathVariable("userId") int userId){
        model.addAttribute("vos",getNews(userId, 0 ,10));
        return "home";
    }


}
