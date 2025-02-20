package com.muses.service.live.handler;

import com.muses.adapter.connection.IConnectionContext;
import com.muses.common.enums.ServerErrorCodeEnums;
import com.muses.common.exception.ServerException;
import com.muses.common.util.JsonFormatter;
import com.muses.domain.live.bo.RtcRoom;
import com.muses.domain.live.bo.SubStream;
import com.muses.domain.servicce.enums.ProtoTypeEnums;
import com.muses.domain.servicce.proto.LeaveEvent;
import com.muses.domain.servicce.proto.LeaveReq;
import com.muses.service.live.context.MediaContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName DispatchHandler
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/17 10:35
 */
@Slf4j
@Component
public class LeaveHandler implements RtcHandler<LeaveReq> {

    @Autowired
    private MediaContext mediaContext;

    @Autowired
    private IConnectionContext connectionContext;

    @Autowired
    private JsonFormatter jsonFormatter;

    @Override
    public String getType() {
        return ProtoTypeEnums.LEAVE.getRtcProtoType();
    }

    @Override
    public void handleMsg(LeaveReq request) {
        log.info("on leaveHandler, user {} leave room {} ", request.getUserId(), request.getRoomId());
        if (StringUtils.isBlank(request.getRoomId())) {
            log.error("user {} can't leave room without roomId", request.getUserId());
            throw ServerException.builder().serverErrorCodeEnums(ServerErrorCodeEnums.PARAM_WRONG).build();
        }
        RtcRoom room = mediaContext.getRoomById(request.getRoomId());
        if (room == null || !room.isRoomActive()) {
            log.info("room not available, ignore this message ");
            return;
        }
        log.info("will notify some people who i subbed ");
        Set<String> mineSubbedUserSet = notifyMySubbedUser(room, request);
        leaveAndNotifySubMyPeople(room, request, mineSubbedUserSet);
        if (!room.hasAliveUser()) {
            mediaContext.closeRoomAfterExit(room.getRoomId());
        }
    }

    private void leaveAndNotifySubMyPeople(RtcRoom room, LeaveReq request, Set<String> mineSubbedUserSet) {
        log.info("will leave the room and notify people who sub my pubStream");
        //通知订阅了我的流挂断流并且关闭peer connection
        Map<String, List<SubStream>> subUserMap = room.leaveAndGetSubStream(request.getUserId());
        subUserMap.forEach((subUserId, subStreamIdList) -> {
            List<String> hangUpStreamIdList = subStreamIdList.stream().map(SubStream::getStreamId).toList();
            log.info("will notify the person who subbed my pubStream to close his stream and peerConnection, notified person is {} will hangup streamIdList {}", subUserId, hangUpStreamIdList);
            LeaveEvent leaveEvent = LeaveEvent.builder()
                    .userId(request.getUserId())
                    .userName(request.getUserName())
                    .hangUpStreamId(hangUpStreamIdList)
                    .build();
            connectionContext.emitEvent(subUserId, leaveEvent);
        });

        //没订阅的人通知退出
        Set<String> userIdSet = room.getOtherUserIds(request.getUserId());
        Set<String> notifyLeaveUserSet = SetUtils.difference(userIdSet, mineSubbedUserSet);
        notifyLeaveUserSet = SetUtils.difference(notifyLeaveUserSet, subUserMap.keySet());
        if(CollectionUtils.isEmpty(notifyLeaveUserSet)){
            log.info("no other in live room, not need notify");
            return;
        }
        LeaveEvent leaveEvent = LeaveEvent.builder()
                .userId(request.getUserId())
                .userName(request.getUserName())
                .build();
        connectionContext.broadcast(notifyLeaveUserSet, leaveEvent);
    }

    private Set<String> notifyMySubbedUser(RtcRoom room, LeaveReq request) {
        log.info("notify people who i {} subbed to close peer connection", request.getUserId());
        Map<String, List<SubStream>> mySubbedUserMap = room.getMySubbedUser(request.getUserId());
        if (MapUtils.isEmpty(mySubbedUserMap)) {
            log.info("i {} not sub any pubStream,don't need notify ", request.getUserId());
            return Collections.emptySet();
        }
        log.info("will notify those people close they peer connection, the notify map {} ", jsonFormatter.object2Json(mySubbedUserMap));

        //通知我订阅了的流关闭peer connection
        mySubbedUserMap.forEach((userId, subStreamList) -> {
            LeaveEvent leaveEvent = LeaveEvent.builder()
                    .userId(request.getUserId())
                    .userName(request.getUserName())
                    .closePeerConnectionList(mySubbedUserMap.get(userId).stream().map(SubStream::getStreamId).toList())
                    .build();
            connectionContext.emitEvent(userId, leaveEvent);
        });
        return mySubbedUserMap.keySet();
    }

}
