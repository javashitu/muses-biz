package com.muses.service.live.rtc.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.muses.adapter.connection.IConnectionContext;
import com.muses.common.enums.LiveCloseReasonEnums;
import com.muses.common.enums.ServerErrorCodeEnums;
import com.muses.common.exception.ServerException;
import com.muses.common.util.JsonFormatter;
import com.muses.domain.live.bo.RtcRoom;
import com.muses.domain.live.bo.RtcUser;
import com.muses.domain.live.bo.SubStream;
import com.muses.domain.servicce.proto.BaseEvent;
import com.muses.domain.servicce.proto.CloseEvent;
import com.muses.domain.servicce.proto.LeaveEvent;
import com.muses.persistence.redis.common.RedisFormatter;
import com.muses.service.live.context.MediaContext;
import com.muses.service.live.rtc.IRtcRoomExtender;
import com.muses.service.live.rtc.IRtcRoomService;
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
 * @ClassName RtcRoomService
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/10 14:22
 */
@Slf4j
@Component
public class RtcRoomService implements IRtcRoomService {

    @Autowired
    private MediaContext mediaContext;

    @Autowired
    private RedisFormatter redisFormatter;

    @Autowired
    private IRtcRoomExtender rtcRoomExtender;

    @Autowired
    private IConnectionContext connectionContext;

    @Autowired
    private JsonFormatter jsonFormatter;

    @Override
    public RtcRoom createRtcRoom(String roomId, String creator) {
        RtcRoom room = RtcRoom.builder()
                .roomId(roomId)
                .name(roomId)
                .creatorId(creator)
                .expireTime(rtcRoomExtender.getInitRoomExpireTime())
                .build();
        mediaContext.getOrCreateRoom(roomId, room);
        log.info("try bind room to redis , room id {}", roomId);
        if (!rtcRoomExtender.initRoomExtender(room)) {
            mediaContext.closeRoomForce(roomId, null);
            log.error("can't bind the live room to redis, ignore this create request");
            throw ServerException.builder().serverErrorCodeEnums(ServerErrorCodeEnums.CANT_CREATE_LIVE_ROOM).build();
        }
        return room;
    }

    @Override
    public Map<String, String> getRoomAddress(String roomId, String userId) {
        return connectionContext.getConnectionUrl(userId, roomId);
    }

    @Override
    public void closeRtcRoom(String roomId, LiveCloseReasonEnums reason) {
        BaseEvent event = CloseEvent.builder()
                .reason(reason.getReason())
                .description(reason.getDesc())
                .build();
        mediaContext.closeRoomForce(roomId, event);
        rtcRoomExtender.removeRomeExtender(roomId);
    }

    @Override
    public Map<String, Object> getRoomInfo(String roomId) {
        Map<String, Object> result = Maps.newHashMap();
        RtcRoom room = mediaContext.getRoomById(roomId);
        if (room == null) {
            log.info("can't find room, maybe room has close");
            result.put("failure", "can't find room");
            return result;
        }
        List<Object> pubStreamList = Lists.newArrayList();
        room.getAllPubStream().forEach((pubStreamId, pubStream) -> {
            Map<String, String> map = Maps.newHashMap();
            map.put("pubStreamId", pubStreamId);
            map.put("pubUserId", pubStream.getPubUserId());
            pubStreamList.add(map);
        });

        result.put("allUserId", room.getAllUserId());
        result.put("pubStream", pubStreamList);
        return result;
    }

    @Override
    public void expelUser(String roomId, String userId) {
        RtcRoom rtcRoom = mediaContext.getRoomById(roomId);
        if (rtcRoom == null || !rtcRoom.isRoomActive()) {
            log.info("rtcRoom has close, not need notify, return");
            return;
        }
        RtcUser rtcUser = rtcRoom.getUser(userId);
        if (rtcUser != null) {
            BaseEvent event = LeaveEvent.builder()
                    .userId(rtcUser.getId())
                    .userName(rtcUser.getName())
                    .build();
            mediaContext.expelUser(roomId, userId, event);
        }
        if (!rtcRoom.isRoomActive()) {
            mediaContext.tryCloseRoom(roomId, LiveCloseReasonEnums.NO_ANCHOR);
        }

    }

    @Override
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

    @Override
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
