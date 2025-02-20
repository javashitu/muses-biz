package com.muses.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName UserExpelReasonEnums
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/13 18:47
 */
@Getter
@AllArgsConstructor
public enum UserExpelReasonEnums {

    TIMEOUT("timeout", "用户连接超时"),
    FORBIDDEN("forbidden", "用户被禁止加入直播间"),
    PAY_LIVE("payLive", "该场直播需要付费加入");

    private final String reason;

    private final String desc;
}
