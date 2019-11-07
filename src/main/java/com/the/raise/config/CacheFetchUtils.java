package com.the.raise.config;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
 
/**
 * @author haozz
 * @date 2018/5/29 15:29
 * @description
 */
@Slf4j
public class CacheFetchUtils {

    public CacheFetchUtils() {
    }

    /**
     * 普通类型缓存方法
     * @param redisOperations
     * @param redisKey
     * @param clazz
     * @param dbFunc
     * @param object
     * @param <T>
     * @return
     */
    public static <T> T fromRedis(RedisOperations redisOperations, String redisKey, Class<T> clazz, Supplier<T> dbFunc, Object... object) {
        T result = redisOperations.getVal(redisKey, clazz);
        if(result == null) {
            result = dbFunc.get();
            if(result == null) {
                log.error("fetch " + clazz + " error, redisKey: " + redisKey);
                return null;
            }
            valSerialize(redisOperations,redisKey,result,object);
        }
        return result;
    }

    /**
     * 集合类型缓存方法
     * @param redisOperations
     * @param redisKey
     * @param clazz
     * @param dbFunc
     * @param object
     * @param <T>
     * @return
     */
    public static <T> List<T> fromRedisList(RedisOperations redisOperations, String redisKey, Class<T> clazz, Supplier<List> dbFunc,Object... object) {
        List<T> result = JSON.parseArray(redisOperations.getVal(redisKey),clazz);
        if(CollectionUtils.isEmpty(result)) {
            result = dbFunc.get();
            if(result == null) {
                log.error("fetch " + clazz + " error, redisKey: " + redisKey);
                return null;
            }
            valSerialize(redisOperations,redisKey,result,object);
        }
        return result;
    }

    /**
     * 指定 KTY 删除缓存
     * @param redisOperations
     * @param redisKey
     */
    public static void cleanRedisByKey(RedisOperations redisOperations,String redisKey){
        redisOperations.clearKey(redisKey);
    }

    /**
     * 指定 KTY* 删除缓存
     * @param redisOperations
     * @param redisKey
     */
    public static void cleanALLRedisByKey(RedisOperations redisOperations,String redisKey){
        redisOperations.clearKeyPattern(redisKey);
    }
 
    private static void valSerialize(RedisOperations redisOperations,String redisKey, Object result,Object... object){
        Object[] objects= object;
        if(objects!=null && objects.length>0){
            Object obj= objects[0];
            if(obj instanceof Date){
                redisOperations.putValExpireAt(redisKey, result, (Date) obj);
            }else {
                Long expireTime= Long.valueOf(obj.toString());
                if(expireTime==0){
                    return;
                }
                redisOperations.putVal(redisKey, result,expireTime);
            }
        }else{
            redisOperations.putVal(redisKey, result);
        }
    }
}