package com.jay.async;

import com.alibaba.fastjson.JSON;
import com.jay.util.JedisAdapter;
import com.jay.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 事件产生器
 */
@Service
public class EventProducer {
    private static final Logger logger = LoggerFactory.getLogger(EventProducer.class);

    @Autowired
    JedisAdapter jedisAdapter;

    /**
     * 发出event
     * @param model event
     * @return 返回执行结果
     */
    public boolean fireEvent(EventModel model){
        try {
            String key = RedisKeyUtil.getEventQueueKey();
            String json = JSON.toJSONString(model); //序列化
            jedisAdapter.lpush(key, json);  //入双向列表
            return true;
        } catch (Exception e) {
            logger.error("发送事件失败: " + e.getMessage());
            return false;
        }
    }

}
