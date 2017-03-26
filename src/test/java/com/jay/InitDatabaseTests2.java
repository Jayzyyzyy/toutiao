package com.jay;

import com.jay.dao.LoginTicketDAO;
import com.jay.dao.NewsDAO;
import com.jay.dao.UserDAO;
import com.jay.model.LoginTicket;
import com.jay.model.News;
import com.jay.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)  //Spring测试环境
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
/*@Sql("/init-schema.sql") */ /*在测试之前执行sql文件*/
public class InitDatabaseTests2 {
	@Autowired
	UserDAO userDAO;

	@Autowired
	NewsDAO newsDAO;

	@Autowired
	LoginTicketDAO loginTicketDAO;

	@Test
	public void initData() {

		LoginTicket loginTicket = loginTicketDAO.selectByUserId(15);
		System.out.println(loginTicket.getTicket());


	}

}
