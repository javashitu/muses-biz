package com.muses.service.live.rtc;

import com.muses.domain.live.bo.RtcRoom;

import java.util.Map;

/**
 * @ClassName IRtcRoomService
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/10 14:38
 */
public interface IRtcRoomService {

    RtcRoom createRtcRoom(String roomId, String creator);

    /**
     * 宽接口，返回的格式类型不定
     */
    Map<String, String> getRoomAddress(String roomId, String userId);

    void closeRtcRoom(String roomId);

    Map<String,Object> getRoomInfo(String roomId);

    void expelUser(String roomId, String userId);

}
