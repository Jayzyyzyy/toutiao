package com.jay.dao;

import com.jay.model.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * messageDAO
 */
@Mapper
public interface MessageDAO {
    String TABLE_NAME = "message";
    String INSERT_FIELDS = " from_id, to_id, content, created_date, has_read, conversation_id ";
    String SELECT_FIELDS = " id,  " + INSERT_FIELDS;

    //添加新闻
    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS, ") values( #{fromId}, #{toId}, #{content}, #{createdDate}, #{hasRead}, #{conversationId})"})
    int addMessage(Message message);

    //某个会话的message
    @Select({"select ", SELECT_FIELDS , " from ", TABLE_NAME, " where conversation_id=#{conversationId}" +
            " order by id desc limit #{offset}, #{limit}"}) //按照时间顺序降序排列
    List<Message> getConversationDetail(@Param("conversationId") String conversationId, @Param("offset") int offset, @Param("limit") int limit);

    //与用户相关的message(复杂的sql语句)
    @Select({"select ", INSERT_FIELDS , ", count(id) as id from " + //每个分组(会话)的个数作为id
            "( select * from ", TABLE_NAME, " where from_id=#{userId} or to_id=#{userId} order by id desc) tt " + //降序从表
            " group by conversation_id order by id desc limit #{offset}, #{limit}"}) //按照时间顺序降序排列，分页
    List<Message> getConversationList(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);

    //获取未读的message数目
    @Select({"select count(id) from ", TABLE_NAME, " where has_read=0 and to_id=#{userId} and conversation_id=#{conversationId}"}) //to_id=userId对我来说未读
    int getConversationUnReadCount(@Param("userId") int userId, @Param("conversationId") String conversationId);

}
