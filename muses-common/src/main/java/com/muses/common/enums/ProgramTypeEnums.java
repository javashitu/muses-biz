package com.muses.common.enums;

import com.muses.common.exception.ServerException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName ProgramTypeEnums
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/7 10:37
 */
@Getter
@AllArgsConstructor
public enum ProgramTypeEnums {

    SHORT_VIDEO("shortVideo", "短视频"),
    LONG_VIDEO("longVideo", "长视频"),

    LIVE("live", "直播"),
    TEXT("text", "图文"),
    VOTE("vote", "投票"),
    ACTIVITY("activity", "活动"),

    ;
    private final String type;

    private final String desc;


    private static final Map<String, ProgramTypeEnums> map = new HashMap<>();

    static{
        for(ProgramTypeEnums programTypeEnum : ProgramTypeEnums.values()){
            map.put(programTypeEnum.getType(), programTypeEnum);
        }
    }

    public static ProgramTypeEnums wrap(String type){
        ProgramTypeEnums programTypeEnum = map.get(type);
        if(programTypeEnum == null){
            throw ServerException.builder().serverErrorCodeEnums(ServerErrorCodeEnums.CANT_CREATE_PROGRAM).build();
        }
        return programTypeEnum;
    }
}
