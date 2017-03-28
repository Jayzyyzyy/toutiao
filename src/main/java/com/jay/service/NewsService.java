package com.jay.service;

import com.jay.dao.NewsDAO;
import com.jay.model.News;
import com.jay.util.ToutiaoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

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

    //插入新闻
    public int addNews(News news) {
        newsDAO.addNews(news);
        return news.getId();
    }

    //根据id获取新闻
    public News getById(int newsId) {
        return newsDAO.getById(newsId);
    }

    //上传图片
    public String saveImage(MultipartFile file) throws IOException {
        int dotPos = file.getOriginalFilename().lastIndexOf("."); //包含路径的文件名
        if (dotPos < 0) {  //不是合法的文件 -1
            return null;
        }
        String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase(); //获得全部小写的扩展名
        if (!ToutiaoUtils.isFileAllowed(fileExt)) { //判断后缀名是否符合要求
            return null;
        }
        //生成随机文件名
        String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
        //file.transferTo();
        Files.copy(file.getInputStream(), new File(ToutiaoUtils.IMAGE_DIR + fileName).toPath(),
                StandardCopyOption.REPLACE_EXISTING);  //复制文件，文件存在则替换
        return ToutiaoUtils.TOUTIAO_DOMAIN + "image?name=" + fileName; //返回文件url，以便后续访问
    }

    //添加评论之后，更新news评论数
    public int updateCommentCount(int id, int count) {
        return newsDAO.updateCommentCount(id, count);
    }



}
