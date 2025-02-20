package com.muses.service.live.rtc;

import com.muses.domain.live.bo.RtcRoom;

/**
 * @ClassName IRtcRoomExtender
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/10 16:07
 */
public interface IRtcRoomExtender {

    long getInitRoomExpireTime();

    boolean initRoomExtender(RtcRoom room);

    boolean removeRomeExtender(String roomId);

    void scanExpireRoom();

    void scanExpireRoomInRedis();

    void scanExpireUser();

    void extendRoomAndUser(String roomId, String userId);

    long getRoomExtendTime();

    long getAnchorExtendTime();

    long getUserExtendTime();
}
