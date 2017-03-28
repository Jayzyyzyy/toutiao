package com.jay.service;

import com.jay.dao.MessageDAO;
import com.jay.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *  messageService
 */
@Service
public class MessageService {

    @Autowired
    MessageDAO messageDAO;

    //添加新闻
    public int addMessage(Message message){
        return messageDAO.addMessage(message);
    }

    //返回两个用户之间的消息列表，并分页
    public List<Message> getConversationDetail(String conversationId, int offset, int limit){
        return messageDAO.getConversationDetail(conversationId, offset, limit);
    }

    //返回与用户相关的所有会话，每个会话显示一条 "letter"
    public List<Message> getConversationList(int userId, int offset, int limit){
        return messageDAO.getConversationList(userId, offset, limit);
    }

    //获取某个会话未读的消息数目
    public int getConversationUnReadCount(int userId, String conversationId){
        return messageDAO.getConversationUnReadCount(userId, conversationId);
    }
}
