package com.jay.service;

import com.alibaba.fastjson.JSONObject;
import com.jay.util.ToutiaoUtils;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 *  上传文件到七牛云服务器
 */
@Service
public class QiniuService {
    private static final Logger logger = LoggerFactory.getLogger(QiniuService.class);
    private static String QINIU_IMAGE_DOMAIN = "http://onh97xzo0.bkt.clouddn.com/"; //图片访问域名

    //设置好账号的ACCESS_KEY和SECRET_KEY
    String ACCESS_KEY = "wxwVp2-SjeaOSYAZvOM5mywg_TwmpZ99YRq4OSER";
    String SECRET_KEY = "DaCncY0g2PUiS-CExBK8LjXB9D87z0wui6HSkCo0";
    //要上传的空间
    String bucketname = "jay-test";

    //密钥配置
    Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
    //创建上传对象
    Configuration cfg = new Configuration(Zone.zone0());
    UploadManager uploadManager = new UploadManager(cfg);


    //简单上传，使用默认策略，只需要设置上传的空间名就可以了。上传凭证
    public String getUpToken() {
        return auth.uploadToken(bucketname);
    }

    public String saveImage(MultipartFile file) throws IOException {
        try {
            int dotPos = file.getOriginalFilename().lastIndexOf(".");
            if (dotPos < 0) {
                return null;
            }
            String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
            if (!ToutiaoUtils.isFileAllowed(fileExt)) {
                return null;
            }

            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
            //调用put方法上传
            Response res = uploadManager.put(file.getBytes(), fileName, getUpToken()); //fileName文件名
            //打印返回的信息
            //System.out.println(res.bodyString());
            if (res.isOK() && res.isJson()) {  //状态正常，返回是一个Json字符串
                return QINIU_IMAGE_DOMAIN + JSONObject.parseObject(res.bodyString()).get("key").toString();
            } else {
                logger.error("七牛异常:" + res.bodyString());
                return null;
            }
        } catch (QiniuException e) {
            // 请求失败时打印的异常的信息
            logger.error("七牛异常:" + e.getMessage());
            return null;
        }
    }
}
