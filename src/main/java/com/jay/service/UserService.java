package com.jay.service;

import com.jay.dao.UserDAO;
import com.jay.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *  service层
 */
@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;

    public User getUser(int id){
        return userDAO.selectById(id);
    }

}
