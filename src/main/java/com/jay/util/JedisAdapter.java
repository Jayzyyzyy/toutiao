package com.jay.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.util.Set;

/**
 *  Redis工具类
 */
@Service
public class JedisAdapter implements InitializingBean{
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    private JedisPool pool = null;

    /**
     * 初始化pool
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("localhost",6379);
    }

    /**
     * 获取Jedis连接源
     * @return Jedis
     */
    public Jedis getJedis(){
        return pool.getResource();
    }

    /**
     * 集合添加元素
     * @param key 集合
     * @param value 元素
     * @return 添加成功，1 ； 元素已存在，0
     */
    public long sadd(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.sadd(key, value);
        }catch (Exception e){
            logger.error("发生异常: " + e.getMessage());
            return 0;
        }finally {
            if(jedis != null){ //还掉连接源
                jedis.close();
            }
        }
    }

    /**
     * 删除value
     * @param key 集合
     * @param value 待删除的值
     * @return 删除成功 1；值不存在，0
     */
    public long srem(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.srem(key, value);
        }catch (Exception e){
            logger.error("发生异常: " + e.getMessage());
            return 0;
        }finally {
            if(jedis != null){ //还掉连接源
                jedis.close();
            }
        }
    }

    /**
     * value是否是集合的元素
     * @param key 集合
     * @param value 元素
     * @return T/F
     */
    public boolean sismember(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.sismember(key, value);
        }catch (Exception e){
            logger.error("发生异常: " + e.getMessage());
            return false;
        }finally {
            if(jedis != null){ //还掉连接源
                jedis.close();
            }
        }
    }

    /**
     *  返回集合中元素的个数
     * @param key 集合
     * @return 个数
     */
    public long scard(String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.scard(key);
        }catch (Exception e){
            logger.error("发生异常: " + e.getMessage());
            return 0;
        }finally {
            if(jedis != null){ //还掉连接源
                jedis.close();
            }
        }
    }

    public static void print(int i, Object obj){
        System.out.println(String.format("%d,%s", i, obj.toString()));
    }

    public static void main(String[] args) {
        Jedis jedis = new Jedis();
        jedis.flushAll(); //清空所有数据

        //Map<String, String>  map = new HashMap<>();
        jedis.set("hello", "world");
        print(1, jedis.get("hello"));
        jedis.rename("hello", "newhello");
        print(1, jedis.get("newhello"));
        jedis.setex("hello2", 15 ,"world"); //设置15s过期时间

        //数值操作 PV page view
        jedis.set("pv", "100");  //数值型操作
        jedis.incr("pv");  //增加1
        print(2, jedis.get("pv"));
        jedis.incrBy("pv", 5); //增加5
        print(2, jedis.get("pv"));

        //列表操作, 最新列表，关注列表
        String listName = "listA";
        for (int i = 0; i < 10; i++) {
            jedis.lpush(listName, "a" + String.valueOf(i));  //左边push元素
        }
        print(3, jedis.lrange(listName, 0 ,12)); //取出范围内的值
        print(4, jedis.llen(listName)); //长度
        print(5, jedis.lpop(listName)); //左边的第一个元素
        print(6, jedis.llen(listName)); //长度
        print(7, jedis.lindex(listName, 3)); //a4
        print(8, jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER, "a4", "xx")); //在指定元素前后插入元素，返回总元素个数
        print(9, jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE, "a4", "bb"));//在指定元素前后插入元素，返回总元素个数
        print(10, jedis.lrange(listName, 0 ,12)); //取出范围内的值

        //Hash表操作,属性个数不确定
        String userKey = "userXX";
        jedis.hset(userKey, "name", "Jim");
        jedis.hset(userKey, "age", "12");
        jedis.hset(userKey, "phone", "18618181818");
        print(12, jedis.hget(userKey, "name"));  //获取某个字段的值
        print(13, jedis.hgetAll(userKey)); //获取所有字段--值
        jedis.hdel(userKey, "phone");
        print(14, jedis.hgetAll(userKey)); //获取所有字段--值
        print(15, jedis.hkeys(userKey)); //获取所有的key
        print(16, jedis.hvals(userKey)); //获取所有的val
        print(17, jedis.hexists(userKey, "email")); //不存在false
        print(18, jedis.hexists(userKey, "age")); //存在 true
        jedis.hsetnx(userKey,"school", "zju"); //如果不存在该字段，则设置字段
        jedis.hsetnx(userKey,"name", "yxy");//如果不存在该字段，则设置字段
        print(19, jedis.hgetAll(userKey));

        //Set集合，共同好友（交）， 点赞(isMember)
        String likeKey1 = "newsLike1";
        String likeKey2 = "newsLike2";
        for (int i = 0; i < 10; i++) {
            jedis.sadd(likeKey1, String.valueOf(i));
            jedis.sadd(likeKey2, String.valueOf(2*i));
        }
        print(20, jedis.smembers(likeKey1)); //列出所有用户
        print(21, jedis.smembers(likeKey2));
        print(22, jedis.sunion(likeKey1, likeKey2)); //求并
        print(23, jedis.sinter(likeKey1, likeKey2)); //求交
        print(24, jedis.sdiff(likeKey1, likeKey2)); //求差
        print(25, jedis.sismember(likeKey1, "5")); //判断5是否是该集合的元素
        jedis.srem(likeKey1, "5");  //删除值"5"
        print(26, jedis.smembers(likeKey1));
        print(27, jedis.scard(likeKey1));
        jedis.smove(likeKey2, likeKey1, "14"); //likeKey2中“14”元素移动到likeKey1中
        print(28, jedis.scard(likeKey1));
        print(29, jedis.smembers(likeKey1));


        //优先队列 Sorted Set 排行榜
        String rankKey = "rankKey";
        jedis.zadd(rankKey, 15, "Jim"); //添加元素
        jedis.zadd(rankKey, 60, "Ben");
        jedis.zadd(rankKey, 90, "Lee");
        jedis.zadd(rankKey, 80, "Mei");
        jedis.zadd(rankKey, 75, "Lucy");

        print(30, jedis.zcard(rankKey)); //总数
        print(31, jedis.zcount(rankKey, 61, 100));  //61至100范围内的元素个数
        print(32, jedis.zscore(rankKey, "Lucy"));  //获取某个元素的score
        jedis.zincrby(rankKey, 2, "Lucy");  //增加Lucy的分数+2
        print(33, jedis.zscore(rankKey, "Lucy"));  //获取某个元素的score
        jedis.zincrby(rankKey, 2, "Luc"); //对于不存在的元素，增加新元素，分数从0开始算
        print(34, jedis.zcount(rankKey, 61, 100)); //3
        print(35, jedis.zcount(rankKey, 0, 100));  //6

        print(36,jedis.zrange(rankKey, 0,-1));
        print(36, jedis.zrange(rankKey, 1, 3)); //顺序第1至3名
        print(37, jedis.zrevrange(rankKey, 1, 3)); //逆序第1至3名

        Set<Tuple> set = jedis.zrangeByScoreWithScores(rankKey, "0", "100");
        for (Tuple tuple : set) {
            print(38, tuple.getElement() + ":" + tuple.getScore());
        }

        print(39, jedis.zrank(rankKey, "Ben"));  //顺序的Ben位置
        print(39, jedis.zrevrank(rankKey, "Ben")); //逆序的Ben位置


        //redis池
        JedisPool pool = new JedisPool();  //Jedis连接池
        for (int i = 0; i < 100; i++) {
            Jedis j = pool.getResource();  //获取jedis连接
            j.get("a");
            System.out.println("POOL" + i); //默认8个连接
            j.close();  //关掉连接
        }

    }
}
