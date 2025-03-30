package com.muses.service.live.handler.service.impl;

import com.muses.adapter.connection.IConnectionContext;
import com.muses.common.util.JsonFormatter;
import com.muses.domain.live.bo.RtcRoom;
import com.muses.domain.live.bo.SubStream;
import com.muses.domain.servicce.proto.LeaveEvent;
import com.muses.service.live.context.MediaContext;
import com.muses.service.live.handler.service.ILiveRoomService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.SetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName LiveRoomService
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2025/3/21 18:14
 */
@Slf4j
@Component
public class LiveRoomService implements ILiveRoomService {

    @Autowired
    private MediaContext mediaContext;

    @Autowired
    private IConnectionContext connectionContext;

    @Autowired
    private JsonFormatter jsonFormatter;


    public void leaveAndNotifySubMyPeople(RtcRoom room, String userId, String userName, Set<String> mineSubbedUserSet) {
        log.info("user {} will leave the room and notify people who sub my pubStream", userId);
        //通知订阅了我的流挂断流并且关闭peer connection
        Map<String, List<SubStream>> subUserMap = room.leaveAndGetSubStream(userId);
        subUserMap.forEach((subUserId, subStreamIdList) -> {
            List<String> hangUpStreamIdList = subStreamIdList.stream().map(SubStream::getStreamId).toList();
            log.info("will notify the person who subbed my pubStream to close his stream and peerConnection, notified person is {} will hangup streamIdList {}", subUserId, hangUpStreamIdList);
            LeaveEvent leaveEvent = LeaveEvent.builder()
                    .userId(userId)
                    .userName(userName)
                    .hangUpStreamId(hangUpStreamIdList)
                    .build();
            connectionContext.emitEvent(subUserId, leaveEvent);
        });

        //没订阅的人通知退出
        Set<String> userIdSet = room.getOtherUserIds(userId);
        Set<String> notifyLeaveUserSet = SetUtils.difference(userIdSet, mineSubbedUserSet);
        notifyLeaveUserSet = SetUtils.difference(notifyLeaveUserSet, subUserMap.keySet());
        if (CollectionUtils.isEmpty(notifyLeaveUserSet)) {
            log.info("no other in live room, not need notify");
            return;
        }
        LeaveEvent leaveEvent = LeaveEvent.builder()
                .userId(userId)
                .userName(userName)
                .build();
        connectionContext.broadcast(notifyLeaveUserSet, leaveEvent);
    }

    public Set<String> notifyMySubbedUser(RtcRoom room, String userId, String userName) {
        log.info("notify people who i {} subbed to close peer connection", userId);
        Map<String, List<SubStream>> mySubbedUserMap = room.getMySubbedUser(userId);
        if (MapUtils.isEmpty(mySubbedUserMap)) {
            log.info("i {} not sub any pubStream,don't need notify ", userId);
            return Collections.emptySet();
        }
        log.info("will notify those people close they peer connection, the notify map {} ", jsonFormatter.object2Json(mySubbedUserMap));

        //通知我订阅了的流关闭peer connection
        mySubbedUserMap.forEach((id, subStreamList) -> {
            LeaveEvent leaveEvent = LeaveEvent.builder()
                    .userId(userId)
                    .userName(userName)
                    .closePeerConnectionList(mySubbedUserMap.get(id).stream().map(SubStream::getStreamId).toList())
                    .build();
            connectionContext.emitEvent(id, leaveEvent);
        });
        return mySubbedUserMap.keySet();
    }
}
