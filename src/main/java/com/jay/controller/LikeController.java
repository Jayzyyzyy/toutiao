package com.jay.controller;

import com.jay.async.EventModel;
import com.jay.async.EventProducer;
import com.jay.async.EventType;
import com.jay.model.EntityType;
import com.jay.model.HostHolder;
import com.jay.service.LikeService;
import com.jay.service.NewsService;
import com.jay.util.ToutiaoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *  点赞、点踩控制器
 */
@Controller
public class LikeController {
    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    NewsService newsService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(value = {"/like"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String like(@RequestParam("newsId") int newsId){
        //登录情况下, 点赞该新闻，获得该新闻总赞数
        long likeCount = likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_NEWS, newsId);
        //更新数据库news likeCount数目
        newsService.updateLikeCount(newsId, (int)likeCount);

        //发送点赞事件
        eventProducer.fireEvent(new EventModel().setEventType(EventType.LIKE).
                setActorId(hostHolder.getUser().getId()).
                setEntityOwnerId(newsService.getById(newsId).getUserId()).
                setEntityType(EntityType.ENTITY_NEWS).setEntityId(newsId));

        return ToutiaoUtils.getJsonString(0, String.valueOf(likeCount));
    }

    @RequestMapping(value = {"/dislike"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String disLike(@RequestParam("newsId") int newsId){
        //登录情况下, 点踩该新闻，获得该新闻总赞数
        long likeCount = likeService.disLike(hostHolder.getUser().getId(), EntityType.ENTITY_NEWS, newsId);
        //更新数据库news likeCount数目
        newsService.updateLikeCount(newsId, (int)likeCount);
        return ToutiaoUtils.getJsonString(0, String.valueOf(likeCount));
    }

}
