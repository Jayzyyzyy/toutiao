package com.jay.controller;

import com.jay.service.NewsService;
import com.jay.util.ToutiaoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 *  新闻控制器
 */
@Controller
public class NewsController {
    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    NewsService newsService;

    @RequestMapping(path = {"/uploadImage/"}, method = {RequestMethod.POST})
    @ResponseBody
    public String uploadImage(@RequestParam("file")MultipartFile file){

        try {
            String fileUrl = newsService.saveImage(file);

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
