package com.muses.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName ConnectionActionEnums
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/11 15:40
 */
@Getter
@AllArgsConstructor
public enum ConnectionActionEnums {

    CONNECT_ACTION("CONNECT_ACTION","连接建立"),
    PING_ACTION("PING_ACTION","接收ping"),
    DISCONNECT_ACTION("DISCONNECT_ACTION","连接断开")
    ;

    private String action;

    private String desc;
}
