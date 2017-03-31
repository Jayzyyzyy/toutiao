package com.jay.util;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.util.Map;
import java.util.Properties;

/**
 *  邮件发送服务
 */
@Service
public class MailSender implements InitializingBean{
    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);

    private JavaMailSenderImpl mailSender;

    @Autowired
    private VelocityEngine velocityEngine;  //渲染引擎, Spring注入

    /**
     *
     * @param to 收件人地址
     * @param subject 邮件主题
     * @param template 模板
     * @param model 数据传入
     * @return 发送成功
     */
    public boolean sendWithHTMLTemplate(String to, String subject,
                                        String template, Map<String, Object> model) {
        try {
            String nick = MimeUtility.encodeText("Jay"); //发件人昵称
            InternetAddress from = new InternetAddress(nick + "<xuweijay@163.com>"); //发件人
            MimeMessage mimeMessage = mailSender.createMimeMessage(); //邮件消息
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage); //构造邮件

            String result = VelocityEngineUtils
                    .mergeTemplateIntoString(velocityEngine, template, "UTF-8", model); //通过模板渲染

            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(result, true);
            mailSender.send(mimeMessage);
            return true;
        } catch (Exception e) {
            logger.error("发送邮件失败" + e.getMessage());
            return false;
        }
    }

    //bean初始化
    @Override
    public void afterPropertiesSet() throws Exception {
        mailSender = new JavaMailSenderImpl();

        // 请输入自己的邮箱和密码，用于发送邮件
        mailSender.setUsername("xuweijay@163.com");
        mailSender.setPassword("1a2b3c4d");
        mailSender.setHost("smtp.163.com"); //服务器
        // 请配置自己的邮箱和密码

        mailSender.setPort(465); //端口号
        mailSender.setProtocol("smtps"); //协议ssl
        mailSender.setDefaultEncoding("utf8");
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.ssl.enable", true);
        mailSender.setJavaMailProperties(javaMailProperties);

    }
}
