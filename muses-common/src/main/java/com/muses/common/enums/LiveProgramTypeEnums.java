package com.muses.common.enums;

import com.muses.common.exception.ServerException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName LiveProgramTypeEnums
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/9 16:44
 */
@Getter
@AllArgsConstructor
public enum LiveProgramTypeEnums {

    MOBILE_AUDIO("mobileAudio","手机语音直播"),

    //默认开启视频时也同时开启语音且不能只开启视频
    MOBILE_VIDEO("mobileVideo","手机视频直播"),

    MOBILE_GAME("mobileGame","手游直播"),

    PC("pc","电脑端直播"),

    VTUBER("VTuber","VTuber直播");

    private final String type;

    private final String desc;

    private static final Map<String, LiveProgramTypeEnums> map = new HashMap<>();

    static {
        for (LiveProgramTypeEnums liveProgramTypeEnums : LiveProgramTypeEnums.values()) {
            map.put(liveProgramTypeEnums.getType(), liveProgramTypeEnums);
        }
    }

    public static LiveProgramTypeEnums wrap(String type) {
        LiveProgramTypeEnums liveProgramTypeEnums = map.get(type);
        if (liveProgramTypeEnums == null) {
            throw ServerException.builder().serverErrorCodeEnums(ServerErrorCodeEnums.CANT_CREATE_PROGRAM).build();
        }
        return liveProgramTypeEnums;
    }
}
