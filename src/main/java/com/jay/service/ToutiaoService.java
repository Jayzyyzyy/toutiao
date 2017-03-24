package com.jay.service;

import org.springframework.stereotype.Service;

/**
 * Created by Jay on 2017/3/22.
 */
@Service  //容器启动时创建
public class ToutiaoService {

    public String say(){
        return "This is from ToutiaoService";
    }

}
