package com.jay.model;

import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class HostHolder {
    private static ThreadLocal<User> users = new ThreadLocal<User>(); //线程本地变量

    public void set(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }


}
