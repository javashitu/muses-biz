package com.muses.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName LiveCloseReasonEnums
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/11 13:51
 */
@Getter
@AllArgsConstructor
public enum LiveCloseReasonEnums {

    NO_USER("NO_USER", "没有用户，直播间结束"),
    ANCHOR_CLOSE("ANCHOR_CLOSE", "主播关闭直播间"),
    NO_ANCHOR("NO_ANCHOR", "找不到主播，直播关闭"),
    NO_ROOM("NO_ROOM", "找不到直播间"),
    ;


    private final String reason;

    private final String desc;
}
