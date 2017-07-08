package com.jay.async.handler;

import com.jay.async.EventHandler;
import com.jay.async.EventModel;
import com.jay.async.EventType;
import com.jay.model.Message;
import com.jay.model.User;
import com.jay.service.MessageService;
import com.jay.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 *  点赞处理器
 */
@Component
public class LikeHandler implements EventHandler{

    @Autowired
    UserService userService;

    @Autowired
    MessageService messageService;

    @Override
    public void doHandle(EventModel model) {
        Message message = new Message();
        User user = userService.getUser(model.getActorId()); //事件触发用户
        message.setFromId(13); //系统账户
        int toId = model.getEntityOwnerId();
        message.setToId(toId); //新闻发布用户
        message.setCreatedDate(new Date());
        message.setContent("用户" + user.getName() + "赞了您的资讯,http://127.0.0.1/news/"+String.valueOf(model.getEntityId()));
        message.setConversationId(13 < toId ? String.format("%s_%s", 13, toId) :  //小的id放在前面
                String.format("%s_%s", toId, 13));
        messageService.addMessage(message);
        //System.out.println("点赞");
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);  //返回集合, 注册需要处理的事件类型
    }
}
