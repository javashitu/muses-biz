package com.muses.common.enums;

import com.muses.common.exception.ServerException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName ProgramCreateTypeEnums
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/8 19:21
 */
@Getter
@AllArgsConstructor
public enum ProgramCreateTypeEnums {
    SELF("self", "自制"),
    /**
     * 分享类节目，如果原始节目被删除，被分享的节目也应该被删除内容，但是保留分享过的记录
     * 原始创建者可以自行删除节目
     */
    SHARE("share", "分享"),
    /**
     * 目前联合仅支持视频联合投稿，活动联合发起，直播联合开通
     * 联合投稿的节目，投稿后需要联合人确认才能展示，删除时也需要联合人确认。每个UP自己的动态下都能看到联合投稿的内容，每个UP都有自己的活动记录
     */
    UNION("union", "联合");


    private final String type;

    private final String desc;

    private static final Map<String, ProgramCreateTypeEnums> map = new HashMap<>();

    static{
        for(ProgramCreateTypeEnums programCreateTypeEnum : ProgramCreateTypeEnums.values()){
            map.put(programCreateTypeEnum.getType(), programCreateTypeEnum);
        }
    }

    public static ProgramCreateTypeEnums wrap(String type){
        ProgramCreateTypeEnums programCreateTypeEnum = map.get(type);
        if(programCreateTypeEnum == null){
            throw ServerException.builder().serverErrorCodeEnums(ServerErrorCodeEnums.CANT_CREATE_PROGRAM).build();
        }
        return programCreateTypeEnum;
    }
}