package com.jay.async;

import com.alibaba.fastjson.JSON;
import com.jay.util.JedisAdapter;
import com.jay.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  事件消费器
 */
@Service
public class EventConsumer implements InitializingBean , ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private ApplicationContext applicationContext; //初始化consumer的时候调用setApplication()方法注入

    Map<EventType, List<EventHandler>> config = new HashMap<EventType, List<EventHandler>>();  //对于每种事件类型，都有哪些handler需要处理

    @Autowired
    JedisAdapter jedisAdapter;

    //初始化
    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class); //bean名为键， bean为值。 获得所有该接口的实现类

        if(beans != null){
            for(Map.Entry<String, EventHandler> entry : beans.entrySet()){
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes(); //每个handler能够处理那些事件类型
                for (EventType eventType : eventTypes) {
                    //刚开始不含任何键
                    if(!config.containsKey(eventType)){
                        config.put(eventType, new ArrayList<EventHandler>());
                    }
                    //注册每个时间的处理函数handler
                    config.get(eventType).add(entry.getValue());
                }
            }
        }

        //启动线程去消费事件
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //从队列(双向列表)一直消费
                while(true){
                    String key = RedisKeyUtil.getEventQueueKey();
                    //返回一个含有两个元素的列表，第一个元素是被弹出元素所属的 key ，第二个元素是被弹出元素的值。
                    List<String> messages = jedisAdapter.brpop(0, key);  //从队列中取出model，没有就一直等待，线程阻塞(监听)

                    for (String message : messages) {
                        //第一个是列表（队列）名字
                        if(message.equals(key)){
                            continue;
                        }

                        EventModel model = JSON.parseObject(message, EventModel.class);
                        //找到这个事件的处理handler列表
                        if(!config.containsKey(model.getEventType())){
                            logger.error("不能识别的事件");
                            continue;
                        }
                        for(EventHandler handler : config.get(model.getEventType())){
                            handler.doHandle(model);
                        }
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException { //注入
        this.applicationContext = applicationContext;
    }
}
