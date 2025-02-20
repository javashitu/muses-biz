package com.muses.service.live.rtc.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.muses.adapter.connection.IConnectionContext;
import com.muses.common.enums.LiveCloseReasonEnums;
import com.muses.common.enums.ServerErrorCodeEnums;
import com.muses.common.exception.ServerException;
import com.muses.domain.live.bo.RtcRoom;
import com.muses.domain.live.bo.RtcUser;
import com.muses.domain.servicce.proto.BaseEvent;
import com.muses.domain.servicce.proto.CloseEvent;
import com.muses.domain.servicce.proto.LeaveEvent;
import com.muses.persistence.redis.common.RedisFormatter;
import com.muses.service.live.context.MediaContext;
import com.muses.service.live.rtc.IRtcRoomExtender;
import com.muses.service.live.rtc.IRtcRoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
    public void closeRtcRoom(String roomId) {
        BaseEvent event = CloseEvent.builder()
                .reason(LiveCloseReasonEnums.ANCHOR_CLOSE.getReason())
                .description(LiveCloseReasonEnums.ANCHOR_CLOSE.getDesc())
                .build();
        mediaContext.closeRoomForce(roomId, event);
        rtcRoomExtender.removeRomeExtender(roomId);
    }

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
        if(!rtcRoom.isRoomActive()){
            mediaContext.tryCloseRoom(roomId, LiveCloseReasonEnums.NO_ANCHOR);
        }

    }
}
