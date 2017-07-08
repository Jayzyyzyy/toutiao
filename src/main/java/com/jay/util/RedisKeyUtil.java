package com.jay.util;

/**
 *  Redis生成key的工具类，生成集合的名称（唯一）
 */
public class RedisKeyUtil {
    private static String SPLIT = ":"; //分隔符
    private static String BIZ_LIKE = "LIKE"; //业务————喜欢
    private static String BIZ_DISLIKE = "DISLIKE";  //业务————不喜欢

    private static String BIZ_EVENT = "EVENT"; //事件名字

    /**
     * 获取事件队列的key
     * @return
     */
    public static String getEventQueueKey(){
        return BIZ_EVENT;
    }

    /**
     * 获取某个实体的喜欢业务集合key
     * @param entityType
     * @param entityId
     * @return
     */
    public static String getLikeKey(int entityType, int entityId){
        return BIZ_LIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    /**
     * 获取某个实体的不喜欢业务集合key
     * @param entityType
     * @param entityId
     * @return
     */
    public static String getDisLikeKey(int entityType, int entityId){
        return BIZ_DISLIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

}
