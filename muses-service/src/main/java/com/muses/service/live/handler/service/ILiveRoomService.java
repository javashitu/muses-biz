package com.muses.service.live.handler.service;

import com.muses.domain.live.bo.RtcRoom;

import java.util.Set;

/**
 * @ClassName ILiveRoomService
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2025/3/21 18:14
 */
public interface ILiveRoomService {

    void leaveAndNotifySubMyPeople(RtcRoom room, String userId, String userName, Set<String> mineSubbedUserSet);

    Set<String> notifyMySubbedUser(RtcRoom room, String userId, String userName);

}
