package com.jay.async;

import java.util.List;

/**
 *  事件处理器接口
 */
public interface EventHandler {
    void doHandle(EventModel model); //处理事件
    List<EventType> getSupportEventTypes();  //该处理器可以处理的事件model类型
}
