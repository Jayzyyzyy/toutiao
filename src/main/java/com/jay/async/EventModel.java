package com.jay.async;

import java.util.HashMap;
import java.util.Map;

/**
 *  队列中的事件model
 */
public class EventModel {
    private EventType eventType; //事件类型
    private int actorId; //触发者(点赞的人)
    private int entityType; //触发的对象类型 id （新闻类型）
    private int entityId;
    private int entityOwnerId; //触发对象的拥有者 （新闻发布者）
    private Map<String, String> exts = new HashMap<String, String>(); //触发事件现场的参数、附带信息

    public EventModel() {
    }

    public EventModel(EventType eventType) {
        this.eventType = eventType;
    }

    public Map<String, String> getExts(){
        return exts;
    }

    public EventModel setExt(String name, String value){
        exts.put(name, value);
        return this;
    }

    public String getExt(String name){
        return exts.get(name);
    }

    public EventType getEventType() {
        return eventType;
    }

    public EventModel setEventType(EventType eventType) {
        this.eventType = eventType;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }
}
