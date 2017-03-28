package com.jay.controller;

import com.jay.model.HostHolder;
import com.jay.model.News;
import com.jay.service.NewsService;
import com.jay.service.QiniuService;
import com.jay.util.ToutiaoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

/**
 *  新闻控制器
 */
@Controller
public class NewsController {
    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    NewsService newsService;

    @Autowired
    QiniuService qiniuService;

    @Autowired
    HostHolder hostHolder;

    /**
     * 发布咨询
     * @param image
     * @param title
     * @param link
     * @return
     */
    @RequestMapping(path = {"/user/addNews/"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String addNews(@RequestParam("image") String image,  //七牛云存储的url自动解析
                          @RequestParam("title") String title,
                          @RequestParam("link") String link){
        try{
            News news = new News();

            if(hostHolder.getUser() != null){
                news.setUserId(hostHolder.getUser().getId());
            }else {
                //设置一个匿名用户id
                news.setUserId(3);
            }
            news.setCreatedDate(new Date());
            news.setImage(image);
            news.setLink(link);
            news.setTitle(title);
            newsService.addNews(news);
            return ToutiaoUtils.getJsonString(0, "资讯发布成功");
        }catch (Exception e){
            logger.error("发布资讯失败: " + e.getMessage());
            return ToutiaoUtils.getJsonString(1, "资讯发布失败");
        }
    }

    /**
     * 图片上传
     * @param file
     * @return
     */
    @RequestMapping(path = {"/uploadImage/"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String uploadImage(@RequestParam("file")MultipartFile file){
        try {
//            String fileUrl = newsService.saveImage(file);
            String fileUrl = qiniuService.saveImage(file);

            if(fileUrl == null){
                return ToutiaoUtils.getJsonString(1, "图片上传失败");
            }
            return ToutiaoUtils.getJsonString(0, fileUrl);
        } catch (IOException e) {
            logger.error("图片上传失败: "+ e.getMessage());
            return ToutiaoUtils.getJsonString(1, "图片上传失败");
        }
    }

    /**
     * 图片访问
     * @param imageName
     * @param response
     */
    @RequestMapping(path = {"/image"}, method = {RequestMethod.GET})
    @ResponseBody
    public void getImage(@RequestParam("name") String imageName, HttpServletResponse response){
        try {
            response.setContentType("image/jpeg");  //设置响应内容的类型
            StreamUtils.copy(new FileInputStream(new File(ToutiaoUtils.IMAGE_DIR + imageName)),
                    response.getOutputStream());  //输出
        } catch (IOException e) {
            logger.error("图片下载出错: " + e.getMessage());
        }
    }



}
