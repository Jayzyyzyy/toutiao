package com.jay.dao;

import com.jay.model.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 *  commentDAO
 */
@Mapper
public interface CommentDAO {
    String TABLE_NAME = "comment";
    String INSERT_FIELDS = " content, user_id, entity_id, entity_type, created_date, status ";
    String SELECT_FIELDS = " id,  " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS, ") values( #{content}, #{userId}, #{entityId}, #{entityType}, #{createdDate}, " +
            "#{status})"})
    int addComment(Comment comment);

    @Update({"update ", TABLE_NAME, " set status = #{status} where entity_id=#{entityId} and entity_type=#{entityType} and id=#{id}"})
    void updateStatus(@Param("entityId") int entityId, @Param("entityType") int entityType, @Param("status") int status, @Param("id") int id);

    //得到某种类型（例如某个新闻）的评论列表
    @Select({"select ", SELECT_FIELDS , " from ", TABLE_NAME, " where entity_id=#{entityId} and entity_type=#{entityType} order by id desc "})
    List<Comment> selectByEntity(@Param("entityId") int entityId, @Param("entityType") int entityType);

    //得到某种类型（例如某个新闻）的评论数目
    @Select({"select count(id) from ", TABLE_NAME, " where entity_id=#{entityId} and entity_type=#{entityType}"})
    int getCommentCount(@Param("entityId") int entityId, @Param("entityType") int entityType);

}
