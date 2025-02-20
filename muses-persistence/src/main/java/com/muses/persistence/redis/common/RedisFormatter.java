package com.muses.persistence.redis.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ClassName RedisFormatter
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/10 10:47
 */
@Slf4j
@Component
public class RedisFormatter {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("set redis key failure key {} -> value {} ", key, value, e);
            return false;
        }
    }

    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("set redis key with expire time failure key {} -> value {} expire on {} second ", key, value, time, e);
            return false;
        }
    }

    public boolean hset(String hashName, String key, Object value) {
        try {
            redisTemplate.opsForHash().put(hashName, key, value);
            return true;
        } catch (Exception e) {
            log.error("set redis hash failure hash name {} key {} -> value {} ", hashName, key, value, e);
            return false;
        }
    }

    /**
     * 这个方法有大坑，如果key已经存在，会返回false, key不存在才返回true，所以返回结果不能视为操作的结果
     */
    public boolean zadd(String setName, String key, long score) {
        try {
            boolean operateFlag = Boolean.TRUE.equals(redisTemplate.opsForZSet().add(setName, key, score));
            log.info("zadd score for set {} key {} -> result {}", setName, key, operateFlag);
            return operateFlag;
        } catch (Exception e) {
            log.error("set redis zset failure zset name {} key {} -> score {} ", setName, key, score, e);
            return false;
        }
    }

    public Map<String, Double> zrangebyscore(String setName, long min, long max) {
        try {
            Set<ZSetOperations.TypedTuple<Object>> set = redisTemplate.opsForZSet().rangeByScoreWithScores(setName, min, max);
            if (CollectionUtils.isEmpty(set)) {
                log.info("no item score between min {} and max {} in set {}", min, max, setName);
                return Collections.emptyMap();
            }
            log.info("zrangebyscore scan {} keys between min {} and max {}", set.size(), min, max);
            return set.stream().map(tuple -> {
                String key = (String) tuple.getValue();
                //理论上这里的值不可能为null,因为zadd时肯定携带了分值
                double score = tuple.getScore();
                return MutablePair.of(key, score);
            }).collect(Collectors.toMap(Pair::getKey, Pair::getValue));
        } catch (Exception e) {
            log.error("range by score failure set name {} min score {} -> max score {} ", setName, min, max, e);
            return Collections.emptyMap();
        }
    }

    public long zrem(String setName, String key) {
        try {
            Long count = redisTemplate.opsForZSet().remove(setName, key);
            log.info("zrem key in set {} key {} ,finally remove count {}", setName, key, count);
            return count == null ? 0 : count;
        } catch (Exception e) {
            log.error("zrem key in set {} remove key {} ", setName, key, e);
            return 0L;
        }
    }

    /**
     * 函数含义和redis指令一样，增加指定分数
     */
    public double zincrby(String setName, String key, double score) {
        try {
            Double result = redisTemplate.opsForZSet().incrementScore(setName, key, score);
            log.info("zincrby key in set {} key {} ,increment score {} and the result score {}", setName, key, score, result);
            return result == null ? 0 : result;
        } catch (Exception e) {
            log.info("zincrby key in set {} key {}  failure ,increment score {}", setName, key, score);
            return 0D;
        }
    }

}
