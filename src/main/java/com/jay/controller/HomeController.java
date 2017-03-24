package com.jay.controller;

import com.jay.dao.NewsDAO;
import com.jay.dao.UserDAO;
import com.jay.model.News;
import com.jay.model.ViewObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Controller
public class HomeController {
    @Autowired
    UserDAO userDAO;

    @Autowired
    NewsDAO newsDAO;

    private List<ViewObject> getNews(int userId, int offset, int limit){
        List<News> newsList = newsDAO.selectByUserIdAndOffset(userId, offset, limit);

        List<ViewObject> vos = new ArrayList<ViewObject>();
        for (News news : newsList) {
            ViewObject vo = new ViewObject();
            vo.set("news", news);
            vo.set("user", userDAO.selectById(news.getUserId()));
            vos.add(vo);
        }
        return vos;
    }

    @RequestMapping(value = {"/","/index"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model){
        model.addAttribute("vos",getNews(0, 0 ,10));
        return "home";
    }

    @RequestMapping(value = {"/user/{userId}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userIndex(Model model, @PathVariable("userId") int userId){
        model.addAttribute("vos",getNews(userId, 0 ,10));
        return "home";
    }


}
