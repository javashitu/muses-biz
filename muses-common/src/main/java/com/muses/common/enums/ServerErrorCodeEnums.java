package com.muses.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName ServerErrorCodeEnums
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/7 10:25
 */
@Getter
@AllArgsConstructor
public enum ServerErrorCodeEnums {
    /**
     * 非0值表示出现异常
     * 规则xx-yyy-zzzz
     * xx 系统 rtc 10
     * yyy 模块 rtc logic 100, connection 200, program 300
     * zzzz 错误码
     */
    SUCCESS("0","SUCCESS"),
    ROOM_NOT_EXITS("101000010", "rtc room not exits"),
    USER_EXITS("101000015", "rtc user not exits"),
    USER_INFO_WRONG("101000020", "user info wrong"),
    PARAM_WRONG("101000025", "param wrong"),
    SIGNAL_TYPE_WRONG("101000030", "signal type wrong"),
    CANT_CREATE_LIVE_ROOM("101000035", "cant create live room"),
    CANT_CLOSE_LIVE_ROOM("101000040", "cant close live room"),
    CANT_FIND_LIVE_PROGRAM("101000045", "cant find live program"),


    CANT_FIND_PROGRAM("103000010", "can't find program"),
    CANT_CREATE_PROGRAM("103000015", "can't create program"),
    CANT_SHARE_PROGRAM("103000020", "can't share program"),
    CANT_FIND_VIDEO_PROGRAM("103000025", "can't find video program"),
    AUTHORIZATION_FAILURE("103000030", "authorization failure"),


    SERVER_ERROR("101000900", "server error")
    ;


    private final String code;

    private final String message;}
