package com.jay.service;

import com.jay.dao.CommentDAO;
import com.jay.model.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *  commentService
 */
@Service
public class CommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    @Autowired
    CommentDAO commentDAO;

    public int addComment(Comment comment){
        return commentDAO.addComment(comment);
    }

    //获取某条新闻的评论列表
    public List<Comment> getCommentsByEntity(int entityId, int entityType){
        return commentDAO.selectByEntity(entityId, entityType);
    }

    //获取某条新闻的评论总数
    public int getCommentsCount(int entityId, int entityType){
        return commentDAO.getCommentCount(entityId, entityType);
    }

    public void deleteComment(int entityId, int entityType, int commentId){
        commentDAO.updateStatus(entityId, entityType, 1, commentId); //状态置为无效
    }
}
