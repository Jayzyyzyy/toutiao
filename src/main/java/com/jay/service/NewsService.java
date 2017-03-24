package com.jay.service;

import com.jay.dao.NewsDAO;
import com.jay.model.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *  news Service
 */
@Service
public class NewsService {

    @Autowired
    private NewsDAO newsDAO;

    /**
     * 读取用户的最近几个新闻
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<News> getLatestNews(int userId, int offset, int limit){
        return newsDAO.selectByUserIdAndOffset(userId, offset, limit);
    }


}
