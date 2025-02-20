package com.muses.common.enums;

/**
 * @ClassName IRedisKeyFormatter
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/10 14:59
 */
public interface ILiveFormatter {
    /**
     * 参数不一定全部要使用，不同场景下使用不同的id
     */
    String format(String... ids);
}
