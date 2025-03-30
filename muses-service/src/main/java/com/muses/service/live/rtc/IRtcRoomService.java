package com.muses.service.live.rtc;

import com.muses.common.enums.LiveCloseReasonEnums;
import com.muses.domain.live.bo.RtcRoom;

import java.util.Map;
import java.util.Set;

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

    /**
     * 关闭所有rtcroom的资源，包括挂断流和维护rtcRoom的所有的连接，以及rtcRoom的计时器
     *
     * @param roomId
     */
    void closeRtcRoom(String roomId, LiveCloseReasonEnums reason);

    Map<String, Object> getRoomInfo(String roomId);

    void expelUser(String roomId, String userId);


    void leaveAndNotifySubMyPeople(RtcRoom room, String userId, String userName, Set<String> mineSubbedUserSet);

    Set<String> notifyMySubbedUser(RtcRoom room, String userId, String userName);

}
