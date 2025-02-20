package com.muses.persistence.redis.common;

import com.muses.common.util.iface.IDistributeLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName RedisLock
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/10 11:19
 */
@Slf4j
@Component
public class RedisLock implements IDistributeLock {

    @Autowired
    private RedisLockRegistry redisLockRegistry;

    @Override
    public boolean tryLock(String lockKey, int lockSeconds) {
        try {
            boolean lockResult = redisLockRegistry.obtain(lockKey).tryLock(lockSeconds, TimeUnit.SECONDS);
            log.info("try lock for key {} in seconds {} ,lock result {}", lockKey, lockSeconds, lockResult);
            return lockResult;
        } catch (Exception e) {
            log.error("try lock in redis failure, lick key {} , lock expire seconds {} ", lockKey, lockSeconds, e);
            return false;
        }
    }

    @Override
    public boolean retryLock(String lockKey, int time, int retryCount) {
        if (tryLock(lockKey, time)) {
            return true;
        }
        for (int i = 0; i < retryCount; i++) {
            log.info("lock failure, begin retry lock, this retry time {} ", i + 1);
            if (tryLock(lockKey, time)) {
                return true;
            }
        }
        log.error("all retry lock has done, but still can't lock, retry lock failure");
        return false;
    }

    @Override
    public void unlock(String lockKey) {
        redisLockRegistry.obtain(lockKey).unlock();
    }
}
