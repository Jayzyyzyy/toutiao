package com.jay.service;

import com.jay.util.JedisAdapter;
import com.jay.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *  likeService
 */
@Service
public class LikeService {
    @Autowired
    JedisAdapter jedisAdapter;

    /**
     * 判断某用户是否喜欢该新闻
     * @param userId
     * @param entityType
     * @param entityId
     * @return 喜欢 1 不喜欢-1 不知道0
     */
    public int getLikeStatus(int userId, int entityType, int entityId){
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        if(jedisAdapter.sismember(likeKey, String.valueOf(userId))){ //判断用户是否喜欢该新闻
            return 1;
        }
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        return jedisAdapter.sismember(disLikeKey, String.valueOf(userId))? -1: 0; //该用户不喜欢该新闻-1
    }

    /**
     * 喜欢新闻动作，返回点赞后的新闻总喜欢数
     * @param userId 用户id
     * @param entityType
     * @param entityId
     * @return
     */
    public long like(int userId, int entityType, int entityId){
        //从喜欢集合里添加该用户id
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        jedisAdapter.sadd(likeKey, String.valueOf(userId));
        //从不喜欢集合里面删除用户id
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        jedisAdapter.srem(disLikeKey, String.valueOf(userId));
        return jedisAdapter.scard(likeKey);
    }

    /**
     * 不喜欢新闻动作，返回点踩后的新闻总喜欢数
     * @param userId 用户id
     * @param entityType
     * @param entityId
     * @return
     */
    public long disLike(int userId, int entityType, int entityId){
        //从不喜欢集合里添加该用户id
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        jedisAdapter.sadd(disLikeKey, String.valueOf(userId));
        //从喜欢集合里面删除用户id
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        jedisAdapter.srem(likeKey, String.valueOf(userId));
        return jedisAdapter.scard(likeKey);
    }
}
