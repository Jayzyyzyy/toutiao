package com.jay;

import com.jay.dao.LoginTicketDAO;
import com.jay.dao.NewsDAO;
import com.jay.dao.UserDAO;
import com.jay.model.News;
import com.jay.model.User;
import com.jay.util.JedisAdapter;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)  //Spring测试环境
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
public class InitDatabaseTests3 {
	@Autowired
	JedisAdapter jedisAdapter;

	@Test
	public void testObject() {

		User user = new User();
		user.setName("Jay");
		user.setPassword("XXX");
		user.setHeadUrl("http://nowcoder.com");
		user.setSalt("userxxx");
		jedisAdapter.setObject("userx", user); //序列化

		User u = jedisAdapter.getObject("userx", User.class); //反序列化

		System.out.println(ToStringBuilder.reflectionToString(u)); //打印该对象
	}

}
