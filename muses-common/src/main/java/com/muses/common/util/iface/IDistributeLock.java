package com.muses.common.util.iface;

/**
 * @ClassName IDistributeLock
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/10 11:08
 */
public interface IDistributeLock {

    boolean tryLock(String lockKey, int lockSeconds);

    boolean retryLock(String lockKey, int time, int retryCount);

    void unlock(String lockKey);

}
