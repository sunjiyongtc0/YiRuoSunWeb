package com.yiruo.common.core.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <pre>
 * Title :  Redis的工具类
 * </pre>
 *
 * @author : SunJiYong
 * @since : 2022-02-28
 **/
@Component
public class RedisUtils {

    private final RedisConfiguration redisConfiguration;

    private final JedisPool pool;

    @Autowired
    public RedisUtils(RedisConfiguration redisConfiguration, JedisPool pool) {
        this.redisConfiguration = redisConfiguration;
        this.pool = pool;
    }

    private void getJedisPassword(Jedis jedis){
//        jedis.auth(redisConfiguration.getPassword());
    }

    /**
     * 持久化数据
     */
    public void psetex(String key, String value) {
        try (Jedis jedis = pool.getResource()) {
            getJedisPassword(jedis);
            jedis.psetex(key, redisConfiguration.getExpiredTime() * 1000L, value);
            // 如果key是对URL授权的角色编码，则不过期
            if (key.contains("EACBASE:URL:ROLECODE")) {
                jedis.persist(key);
            }
        }
    }

    /**
     * 持久化数据
     *
     * @param key     主键
     * @param value   值
     * @param timeout 超时时间(秒)
     */
    public void setex(String key, String value, long timeout) {
        try (Jedis jedis = pool.getResource()) {
            getJedisPassword(jedis);
            jedis.setex(key, timeout, value);
        }
    }

    /**
     * 存放 key value 对到 redis
     * 如果 key 已经持有其他值， SET 就覆写旧值，无视类型。
     * 对于某个原本带有生存时间（TTL）的键来说， 当 SET 命令成功在这个键上执行时， 这个键原有的 TTL 将被清除。
     */
    public void set(String key, String value) {
        try (Jedis jedis = pool.getResource()) {
            getJedisPassword(jedis);
            jedis.set(key, value);
        }
    }

    /**
     * 将哈希表 key 中的域 field 的值设为 value 。
     * 如果 key 不存在，一个新的哈希表被创建并进行 HSET 操作。
     * 如果域 field 已经存在于哈希表中，旧值将被覆盖。
     */
    public Long hset(String key, String field, String value) {
        try (Jedis jedis = pool.getResource()) {
            getJedisPassword(jedis);
            return jedis.hset(key, field, value);
        }
    }

    /**
     * 返回哈希表 key 中给定域 field 的值。
     */
    public String hget(String key, String field) {
        try (Jedis jedis = pool.getResource()) {
            getJedisPassword(jedis);
            return jedis.hget(key, field);
        }
    }

    /**
     * 根据键删除数据
     *
     * @param key 键
     */
    public void del(String key){
        try (Jedis jedis = pool.getResource()) {
        getJedisPassword(jedis);
        jedis.del(key);
        }
    }
    /**
     * 根据键删除数据
     *
     * @param key 键
     */
    public void del(Collection<String> key) {
        try (Jedis jedis = pool.getResource()) {
            getJedisPassword(jedis);
            for(String k:key){
                jedis.del(k);
            }
        }
    }
    /**
     * 根据键获取值
     *
     * @param key 键
     */
    public String get(String key) {
        try (Jedis jedis = pool.getResource()) {
            getJedisPassword(jedis);
            return jedis.get(key);
        }

    }

    /**
     * 这个命令类似于 TTL 命令，但它以毫秒为单位返回 key 的剩余生存时间，而不是像 TTL 命令那样，以秒为单位。
     */
    public Long pttl(String key) {
        try (Jedis jedis = pool.getResource()) {
            getJedisPassword(jedis);
            return jedis.pttl(key);
        }
    }

    /**
     * 这个命令和 EXPIRE 命令的作用类似，但是它以毫秒为单位设置 key 的生存时间，而不像 EXPIRE 命令那样，以秒为单位。
     */
    public Long pexpire(String key, long milliseconds) {
        try (Jedis jedis = pool.getResource()) {
            getJedisPassword(jedis);
            return jedis.pexpire(key, milliseconds);
        }
    }

    /**
     * 将一个或多个值 value 插入到列表 key 的表头
     * 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表头： 比如说，
     * 对空列表 mylist 执行命令 LPUSH mylist a b c ，列表的值将是 c b a ，
     * 这等同于原子性地执行 LPUSH mylist a 、 LPUSH mylist b 和 LPUSH mylist c 三个命令。
     * 如果 key 不存在，一个空列表会被创建并执行 LPUSH 操作。
     * 当 key 存在但不是列表类型时，返回一个错误。
     */
    public Long lpush(String key, String... values) {
        try (Jedis jedis = pool.getResource()) {
            getJedisPassword(jedis);
            return jedis.lpush(key, values);
        }
    }

    public Collection<String> keys(String key) {
        try (Jedis jedis = pool.getResource()) {
            getJedisPassword(jedis);
            return jedis.keys(key);
        }
    }


    //添加自定义方法
    public  void set(String key, Object o){
        try (Jedis jedis = pool.getResource()) {
            getJedisPassword(jedis);
            String j=JSON.toJSONString(o);
            jedis.set(key, j);
        }
    }

    public <T> void setList(String key, List<T> list){
        try (Jedis jedis = pool.getResource()) {
            getJedisPassword(jedis);
            JSONArray array= JSONArray.parseArray(JSON.toJSONString(list));
            jedis.set(key, array.toJSONString());
        }
    }

    public void setObjectDataEx(String key, Object o, long timeout) {
        try (Jedis jedis = pool.getResource()) {
            getJedisPassword(jedis);
            String j=JSON.toJSONString(o);
            jedis.setex(key, timeout, j);
        }
    }

    public  <T> T getObjectData(String key, Class<T> t){
        try (Jedis jedis = pool.getResource()) {
            getJedisPassword(jedis);
            String j=jedis.get(key);
            return JSON.parseObject(j,t);
        }
    }

    public  <T> List<T> getObjectDataList(String key, Class<T> t){
        try (Jedis jedis = pool.getResource()) {
            getJedisPassword(jedis);
            String j=jedis.get(key);
            JSONArray ja=JSONArray.parseArray(j);
            List<T> list=new ArrayList<T>();
            for(int i=0;i<ja.size();i++){
                JSONObject jo=ja.getJSONObject(i);
                list.add(JSON.parseObject(jo.toJSONString(),t));
            }
            return list;
        }
    }
}
