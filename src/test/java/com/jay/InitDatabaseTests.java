package com.jay;

import com.jay.dao.CommentDAO;
import com.jay.dao.LoginTicketDAO;
import com.jay.dao.NewsDAO;
import com.jay.dao.UserDAO;
import com.jay.model.*;
import com.jay.service.CommentService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)  //Spring测试环境
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
@Sql("/init-schema.sql")  /*在测试之前执行sql文件，清空数据库之前数据*/
public class InitDatabaseTests {
	@Autowired
	UserDAO userDAO;

	@Autowired
	NewsDAO newsDAO;

	@Autowired
	LoginTicketDAO loginTicketDAO;

	@Autowired
	CommentDAO commentDAO;

	@Test
	public void initData() {
		Random random = new Random();

		for (int i = 0; i < 10; i++) {

			User user = new User();
			user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
			user.setName(String.format("USER%d", i));
			user.setPassword("");
			user.setSalt("");

			userDAO.addUser(user);  //增

			user.setPassword("newpassword");
			userDAO.updatePassword(user);  //改，如果主键已有，会抛出异常

			News news = new News();
			news.setCommentCount(i);
			Date date = new Date();
			date.setTime(date.getTime() + 1000*3600*5*i);
			news.setCreatedDate(date);
			news.setImage(String.format("http://images.nowcoder.com/head/%dm.png", random.nextInt(1000)));
			news.setLikeCount(i+1);
			news.setUserId(i+1);
			news.setTitle(String.format("TITLE{%d}", i));
			news.setLink(String.format("http://www.nowcoder.com/%d.html", i));
			newsDAO.addNews(news);

			//每个资讯下面插入3条评论
			for (int j = 0; j < 3; j++) {
				Comment comment = new Comment();
				comment.setUserId(i+1);
				comment.setContent("这里是一条评论啊" + String.valueOf(j));
				comment.setEntityId(news.getId());
				comment.setEntityType(EntityType.ENTITY_NEWS);
				comment.setStatus(0); //有效
				comment.setCreatedDate(new Date());
				commentDAO.addComment(comment);
			}



			LoginTicket ticket = new LoginTicket();
			ticket.setStatus(0);
			ticket.setUserId(i+1);
			ticket.setExpired(date);
			ticket.setTicket(String.format("TICKET%d", i+1));
			loginTicketDAO.addTicket(ticket);

			loginTicketDAO.updateStatus(ticket.getTicket(), 2);

		}
		Assert.assertEquals("newpassword", userDAO.selectById(1).getPassword());  //查

		userDAO.deleteById(1);  //删
		Assert.assertNull(userDAO.selectById(1));  //判断是否为空

		Assert.assertEquals(1, loginTicketDAO.selectByTicket("TICKET1").getUserId());  //id
		Assert.assertEquals(2, loginTicketDAO.selectByTicket("TICKET1").getStatus());  //status

		Assert.assertNotNull(commentDAO.selectByEntity(1, EntityType.ENTITY_NEWS).get(0)); //新闻id为1的所有评论

	}

}
