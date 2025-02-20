package com.muses.service.live.context;

import com.muses.common.enums.LiveCloseReasonEnums;
import com.muses.domain.live.bo.RtcRoom;
import com.muses.domain.live.bo.RtcUser;
import com.muses.domain.servicce.proto.BaseEvent;

import java.util.Map;

/**
 * @ClassName IMediaContext
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/11/22 16:07
 */
public interface IMediaContext {
    RtcRoom getOrCreateRoom(String roomId, RtcRoom rtcRoom);

    Map<String, RtcRoom> getAllRoom();

    RtcRoom getRoomById(String roomId);

    RtcUser getUser(String roomId, String userId);

    void boardcast(String roomId, BaseEvent baseEvent);

    void boardcastExclusion(String roomId, String exclusionUserId, BaseEvent baseEvent);

    boolean tryCloseRoom(String roomId, LiveCloseReasonEnums reasonEnums);

    boolean closeRoomAfterExit(String roomId);


    RtcRoom closeRoomForce(String roomId, BaseEvent event);

    void expelUser(String roomId, String userId, BaseEvent event);

    void extendRoomTimeOut(String roomId, long timeOut);

    boolean isRoomActive(String roomId);
}
