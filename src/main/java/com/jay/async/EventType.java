package com.jay.async;

/**
 *  EventType事件类型枚举类
 */
public enum EventType {
    LIKE(0), COMMENT(1), LOGIN(2), MAIL(3);

    private int value;
    EventType(int value){  //构造器私有化
        this.value = value;
    }

    public int getValue(){ //公有方法
        return value;
    }

}
