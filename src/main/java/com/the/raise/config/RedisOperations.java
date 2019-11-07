package com.the.raise.config;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.CharsetNames;
import org.apache.ibatis.annotations.Mapper;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.TimeUnit;
/**
 * @author haozz
 * @date 2018/5/29 15:29
 * @description
 */
@Slf4j
@Configuration
@EnableCaching
public class RedisOperations {
    @Autowired
    private StringRedisTemplate redisTemplate;
 
    public RedisOperations(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
 
    private BoundValueOperations<String, String> boundOptions(String key) {
        return this.redisTemplate.boundValueOps(key);
    }
 
    public <T> List<T> getMultiVal(String keyPattern, Class<T> clazz) {
        return this.getMultiVal(this.keySet(keyPattern), clazz);
    }
 
    public <T> List<T> getMultiVal(Collection<String> keys, Class<T> clazz) {
        ArrayList result = new ArrayList();
        if (null != keys && !keys.isEmpty()) {
            byte[][] rawKeys = new byte[keys.size()][];
            int counter = 0;
 
            String key;
            for (Iterator rawValues = keys.iterator(); rawValues.hasNext(); rawKeys[counter++] = key.getBytes()) {
                key = (String) rawValues.next();
            }
            List var9 = this.redisTemplate.execute((connection) ->
            {
                return connection.mGet(rawKeys);
            }, true);
            Iterator var10 = var9.iterator();
 
            while (var10.hasNext()) {
                byte[] bytes = (byte[]) var10.next();
                if(bytes!=null && bytes.length>0){
                    if(clazz==String.class){
                        try {
                            result.add(new String(bytes,CharsetNames.UTF_8));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }else {
                        result.add(JSON.parseObject(bytes, clazz));
                    }
                }
            }
            return result;
        }
        else {
            return result;
        }
    }
 
    public <V> void putVal(String key, V value, long expireSeconds) {
        BoundValueOperations operation = this.boundOptions(key);
        if (operation != null) {
            operation.set(JSON.toJSONString(value, new SerializerFeature[0]));
            operation.expire(expireSeconds, TimeUnit.SECONDS);
        }
    }
 
    public <V> void putVal(String key, V value) {
        this.boundOptions(key).set(JSON.toJSONString(value));
    }
 
    public <V> void putValExpireAt(String key, V value, Date date) {
        BoundValueOperations operation = this.boundOptions(key);
        if (operation != null) {
            operation.set(JSON.toJSONString(value, new SerializerFeature[0]));
            operation.expireAt(date);
        }
    }
 
    public <T> T getVal(String key, Class<T> clazz) {
        return this.redisTemplate.execute((connection) ->
        {
            byte[] bytes = connection.get(key.getBytes());
            return null != bytes ? JSON.parseObject(bytes, clazz, new Feature[0]) : null;
        }, true);
    }
 
    public String getVal(String key) {
        return this.redisTemplate.execute((connection) ->
        {
            byte[] bytes = connection.get(key.getBytes());
            try {
                return null != bytes ? new String(bytes, CharsetNames.UTF_8) : null;
            }
            catch (UnsupportedEncodingException e) {
                log.error("UnsupportedEncodingException:{}" + e);
            }
            return null;
        }, true);
    }
 
    public Set<String> allKeys(String key){
        Set<String> set = keySet(key + ":*");
        set.add(key);
        return set;
    }
 
    public Set<String> keySet(String pattern) {
        try {
            return this.redisTemplate.keys(pattern);
        }
        catch (Exception var3) {
            throw new RuntimeException("redis service error: {}", var3);
        }
    }
 
    public boolean addKeyExpire(String key, long delta) {
        Long newExpire = this.redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return this.redisTemplate.expire(key, newExpire.longValue() + delta, TimeUnit.SECONDS).booleanValue();
    }
 
    public void clearKey(String... keys) {
        this.clearKey(Arrays.asList(keys));
    }
 
    public void clearKey(Collection<String> keys) {
        try {
            this.redisTemplate.delete(keys);
        }
        catch (Exception var3) {
            throw new RuntimeException("redis service error: {}", var3);
        }
    }
 
    public void clearKeyPattern(String key) {
        Set<String> set = keySet(key + ":*");
        set.add(key);
        this.clearKey(set);
    }
 
    public T get(String tableName, String key, Class<T> clazz) {
        try {
            return JSON.parseObject(hashOps(tableName).get(key).toString(), clazz);
        }
        catch (Exception e) {
            log.error("get{}:{} error.", tableName, key, e);
        }
        return null;
    }
 
    public void put(String tableName, String key, Object value) {
        try {
            hashOps(tableName).put(key, value);
        }
        catch (Exception e) {
            log.error("put{}:{}:{} error.", tableName, key, value, e);
        }
    }
 
    private BoundHashOperations hashOps(String tableName) {
        return redisTemplate.boundHashOps(tableName);
    }
 
    public StringRedisTemplate getRedisTemplate() {
        return redisTemplate;
    }
 
    public Object memory() {
 
        return redisTemplate.execute(new RedisCallback()
        {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
//        connection.bgSave(); // 生成快照
                return connection.info("memory");
            }
        });
    }
 
    /**
     * 存储REDIS队列 顺序存储
     * channel：对列的名称
     * message：消息内容
     * return:消息的存放位置（list中的位置）
     */
    public Long leftPush(String channel, Message message) {
        return redisTemplate.opsForList().leftPush(channel, JSON.toJSONString(message));
    }
 
    /**
     * 存储REDIS队列 顺序存储
     * channel：对列的名称
     * message：消息内容
     * return:消息的内容
     * 弹出最左边的元素，弹出之后该值在列表中将不复存在
     */
    public Message leftPop(String channel) {
        String message = redisTemplate.opsForList().rightPop(channel);
        if (StringUtils.isEmpty(message)) {
            return null;
        }
        Message parseObject = JSON.parseObject(message, Message.class);
        return parseObject;
    }
 
    /**
     * @param channel
     * @return 查看队列中的数据
     * @since 2018年1月12日
     * @see
     */
    public List<String> rangeList(String channel) {
        return redisTemplate.opsForList().range(channel, 0, -1);
 
    }
 
    public Long listSize(String channel) {
        return redisTemplate.opsForList().size(channel);
 
    }
 
}