package com.jay;

import com.jay.dao.NewsDAO;
import com.jay.dao.UserDAO;
import com.jay.model.News;
import com.jay.model.User;
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
@Sql("/init-schema.sql")  /*在测试之前执行sql文件*/
public class InitDatabaseTests {
	@Autowired
	UserDAO userDAO;

	@Autowired
	NewsDAO newsDAO;

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

		}
		Assert.assertEquals("newpassword", userDAO.selectById(1).getPassword());  //查

		userDAO.deleteById(1);  //删
		Assert.assertNull(userDAO.selectById(1));  //判断是否为空



	}

}
