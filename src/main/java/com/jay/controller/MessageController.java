package com.jay.controller;

import com.jay.model.HostHolder;
import com.jay.model.Message;
import com.jay.model.User;
import com.jay.model.ViewObject;
import com.jay.service.MessageService;
import com.jay.service.UserService;
import com.jay.util.ToutiaoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *  message Controller
 */
@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    HostHolder hostHolder; //登录状态检查

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @RequestMapping(path = {"/msg/list"}, method = {RequestMethod.GET})
    public String conversationList(Model model) {
        try {
            int localUserId = hostHolder.getUser().getId();  //获取用户id
            List<Message> conversationList = messageService.getConversationList(localUserId, 0, 10);  //获取到某个用户的所有会话message
            List<ViewObject> conversations = new ArrayList<ViewObject>(); //放到vos中
            for (Message message : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("conversation", message); //会话
                int targetId = message.getFromId()==localUserId? message.getToId() : message.getFromId() ;  //与用户相对于的另一个用户id
                User user=  userService.getUser(targetId);
                if(user == null){
                    continue;
                }
                vo.set("headUrl", user.getHeadUrl());
                vo.set("userName", user.getName());
                vo.set("targetId",targetId);
                vo.set("totalCount", message.getId()); //数据库操作时做了处理
                vo.set("unreadCount", messageService.getConversationUnReadCount(localUserId, message.getConversationId())); //对我localUserId来说还有几条未读取
                conversations.add(vo);
            }
            model.addAttribute("conversations", conversations);
            return "letter";
        } catch (Exception e) {
            logger.error("获取站内信列表失败: " + e.getMessage());
        }
        return "letter";
    }

    @RequestMapping(path = {"/msg/detail"}, method = {RequestMethod.GET})
    public String conversationDetail(Model model, @RequestParam("conversationId") String conversationId) {
        try {
            List<Message> conversationList = messageService.getConversationDetail(conversationId, 0, 10);  //获取到某个会话的所有message
            List<ViewObject> messages = new ArrayList<ViewObject>(); //放到vos中
            for (Message message : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("message", message);
                User user = userService.getUser(message.getFromId()); //来自哪位用户
                if(user == null){
                    continue;
                }
                vo.set("headUrl", user.getHeadUrl());  //头像
                vo.set("userName", user.getName());    //名字
                messages.add(vo);
            }
            model.addAttribute("messages", messages);
            return "letterDetail";
        } catch (Exception e) {
            logger.error("获取站内信列表失败: " + e.getMessage());
        }
        return "letterDetail";
    }


    @RequestMapping(path = {"/msg/addMessage"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String addMessage(@RequestParam("fromId") int fromId, @RequestParam("toId") int toId,
                             @RequestParam("content") String content ) {
        try {
            Message message = new Message();
            message.setContent(content);
            message.setCreatedDate(new Date());
            message.setFromId(fromId);
            message.setToId(toId);
            message.setConversationId(fromId < toId ? String.format("%s_%s", fromId, toId) :  //小的id放在前面
                    String.format("%s_%s", toId, fromId));
            messageService.addMessage(message);
            return ToutiaoUtils.getJsonString(message.getId()); //返回id
        } catch (Exception e) {
            logger.error("添加站内信失败: " + e.getMessage());
            return ToutiaoUtils.getJsonString(1, "添加站内信失败");
        }
    }
}
