package com.jay.async.handler;

import com.jay.async.EventHandler;
import com.jay.async.EventModel;
import com.jay.async.EventType;
import com.jay.model.Message;
import com.jay.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 登陆异常handler
 */
@Component
public class LoginExceptionHandler implements EventHandler{
    @Autowired
    MessageService messageService;



    @Override
    public void doHandle(EventModel model) {
        //判断是否异常登陆
        Message message = new Message();
        int toId = model.getActorId();
        message.setToId(toId);
        message.setFromId(13); //系统帐户
        message.setContent("您上次的登录IP异常");
        message.setCreatedDate(new Date());
        message.setConversationId(13 < toId ? String.format("%s_%s", 13, toId) :  //小的id放在前面
                String.format("%s_%s", toId, 13));
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
