package com.jay.controller;

import com.jay.model.*;
import com.jay.service.*;
import com.jay.util.ToutiaoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.swing.text.View;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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


    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @Autowired
    LikeService likeService;

    @RequestMapping(path = {"/addComment"}, method = {RequestMethod.POST})
    public String addComment(@RequestParam("content") String content,
                            @RequestParam("newsId") int newsId) {
        try {
            //过滤content
            Comment comment = new Comment();
            comment.setCreatedDate(new Date());
            comment.setContent(content);
            comment.setEntityId(newsId);
            comment.setEntityType(EntityType.ENTITY_NEWS);
            comment.setStatus(0);
            comment.setUserId(hostHolder.getUser().getId()); //登录状态下获得，未登录时先登录
            commentService.addComment(comment); //数据库更新

            //增加评论之后，更新新闻评论数commentCount。 在后面通过异步实现
            int count = commentService.getCommentsCount(newsId, EntityType.ENTITY_NEWS); //获取该新闻下的评论数目
            newsService.updateCommentCount(newsId , count);
        } catch (Exception e) {
           logger.error("增加评论失败: " + e.getMessage());
        }

        return "redirect:/news/" + String.valueOf(newsId);  //回到当前页
    }

    /**
     * 获取资讯详情
     * @param newsId
     * @param model
     * @return
     */
    @RequestMapping(path = {"/news/{newsId}"}, method = {RequestMethod.GET})
    public String newsDetail(@PathVariable("newsId") int newsId, Model model) {
        try {
            News news = newsService.getById(newsId);
            //新闻存在
            if(news != null){
                int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0 ;
                if(localUserId != 0){
                    model.addAttribute("like", likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS,news.getId()));
                }else {
                    model.addAttribute("like", 0);
                }
                //评论
                List<Comment> comments = commentService.getCommentsByEntity(news.getId(), EntityType.ENTITY_NEWS);
                List<ViewObject> commentVOs = new ArrayList<ViewObject>();
                for (Comment comment : comments) {
                    if(comment.getStatus() == 1){  //如果已经删除，则直接进入下一个
                        continue;
                    }
                    ViewObject viewObject  =new ViewObject();
                    viewObject.set("comment", comment); //用户
                    viewObject.set("user", userService.getUser(comment.getUserId())); //评论
                    commentVOs.add(viewObject);
                }
                model.addAttribute("comments", commentVOs);
            }
            //添加新闻、用户到model
            model.addAttribute("news", news);
            model.addAttribute("owner", userService.getUser(news.getUserId()));
        } catch (Exception e) {
            logger.error("获取咨询明细错误: "+ e.getMessage());
        }
        return "detail";
    }

    /**
     * 发布资讯
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

}
