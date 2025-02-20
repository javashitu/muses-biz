package com.muses.service.live.context;

import com.muses.adapter.connection.IConnectionContext;
import com.muses.common.enums.LiveCloseReasonEnums;
import com.muses.common.enums.ServerErrorCodeEnums;
import com.muses.common.exception.ServerException;
import com.muses.domain.live.bo.RtcRoom;
import com.muses.domain.live.bo.RtcUser;
import com.muses.domain.servicce.proto.BaseEvent;
import com.muses.domain.servicce.proto.CloseEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName MediaContext
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/17 10:20
 * 如果改变房间的状态，一定要从这里进入
 * mediaContext里封装基础能力，创建，销毁，延时房间，添加，驱逐用户，实际的逻辑组装和触发由service和handler来完成
 */
@Slf4j
@Component
public class MediaContext implements IMediaContext {

    @Autowired
    private IConnectionContext connectionContext;

    private static final ConcurrentHashMap<String, RtcRoom> ROOM_MAP = new ConcurrentHashMap<>();

    private final Lock lock = new ReentrantLock();

    public RtcRoom getOrCreateRoom(String roomId, RtcRoom rtcRoom) {
        return ROOM_MAP.compute(roomId, (key, value) -> {
            if (value != null) {
                log.info("this room has already exit,won't create the room ,return the exist room");
                return value;
            }
            return rtcRoom;
        });
    }

    public Map<String, RtcRoom> getAllRoom() {
        return ROOM_MAP;
    }

    public RtcRoom getRoomById(String roomId) {
        return ROOM_MAP.get(roomId);
    }

    public RtcUser getUser(String roomId, String userId) {
        RtcRoom rtcRoom = getRoomById(roomId);
        if (rtcRoom == null) {
            throw ServerException.builder().serverErrorCodeEnums(ServerErrorCodeEnums.ROOM_NOT_EXITS).build();
        }
        return rtcRoom.getUser(userId);
    }

    public void boardcast(String roomId, BaseEvent baseEvent) {
        RtcRoom rtcRoom = getRoomById(roomId);
        if (rtcRoom == null) {
            throw ServerException.builder().serverErrorCodeEnums(ServerErrorCodeEnums.ROOM_NOT_EXITS).build();
        }
        connectionContext.broadcast(rtcRoom.getAllUserId(), baseEvent);
    }

    public void boardcastExclusion(String roomId, String exclusionUserId, BaseEvent baseEvent) {
        RtcRoom rtcRoom = getRoomById(roomId);
        if (rtcRoom == null) {
            throw ServerException.builder().serverErrorCodeEnums(ServerErrorCodeEnums.ROOM_NOT_EXITS).build();
        }
        connectionContext.broadcast(rtcRoom.getOtherUserIds(exclusionUserId), baseEvent);
    }

    public boolean closeRoomAfterExit(String roomId) {
        lock.lock();
        try {
            RtcRoom rtcRoom = getRoomById(roomId);
            if (rtcRoom == null) {
                log.info("can't find room in server,so can't close , roomId {}", roomId);
                return false;
            }
            if (rtcRoom.hasAliveUser()) {
                log.info("still hsa alive user in room can't close , roomId {}", roomId);
                return false;
            }
            boolean closeResult = rtcRoom.forceCloseRoom();
            log.info("close room after user exit close result is {}", closeResult);
            return closeResult;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean tryCloseRoom(String roomId, LiveCloseReasonEnums reasonEnum) {
        lock.lock();
        try {
            if (isRoomActive(roomId)) {
                log.info("rtc room still active, can't close, do nothing in tryClose");
                return false;
            }
            RtcRoom rtcRoom = ROOM_MAP.remove(roomId);
            if (rtcRoom == null) {
                log.info("can't find room in server,so can't try close , roomId {}", roomId);
                return false;
            }

            boolean closeResult = rtcRoom.forceCloseRoom();
            CloseEvent event = CloseEvent.builder()
                    .rtcRoomId(roomId)
                    .reason(reasonEnum.getReason())
                    .description(reasonEnum.getDesc())
                    .build();
            if (closeResult) {
                rtcRoom.getAllUserId().forEach(userId -> connectionContext.closeById(userId, event));
            }
            return closeResult;
        } finally {
            lock.unlock();
        }
    }

    public RtcRoom closeRoomForce(String roomId, BaseEvent event) {
        RtcRoom rtcRoom = ROOM_MAP.remove(roomId);
        if (rtcRoom == null) {
            log.info("can't find room in server, may be room in other server or has been close , roomId {}", roomId);
            return null;
        }
        Set<String> userIdSet = rtcRoom.getAllUserId();
        if (CollectionUtils.isEmpty(userIdSet)) {
            log.info("no user in room, may be all user has exited or no user enter");
            return rtcRoom;
        }
        userIdSet.forEach(userId -> expelUser(roomId, userId, event));
        return rtcRoom;
    }

    @Override
    public void expelUser(String roomId, String userId, BaseEvent event) {
        RtcRoom rtcRoom = ROOM_MAP.get(roomId);
        if (rtcRoom == null) {
            log.info("can't find room in server, may be room in other server or has been close , roomId {}", roomId);
            return;
        }
        log.info("expelUser of {}", userId);
        rtcRoom.leaveAndGetSubStream(userId);
        connectionContext.closeById(userId, event);
    }

    @Override
    public void extendRoomTimeOut(String roomId, long timeOut) {
        lock.lock();
        try {
            RtcRoom rtcRoom = getRoomById(roomId);
            if (rtcRoom == null) {
                log.info("can't find rtcRoom by roomId {} ,won't extend timeOut", roomId);
                return;
            }
            if (rtcRoom.isRoomActive()) {
                rtcRoom.extendRoomTimeout(timeOut);
            } else {
                log.info("rtcRoom not active, won't extend timeout of room {}", roomId);
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean isRoomActive(String roomId) {
        lock.lock();
        try {
            RtcRoom rtcRoom = getRoomById(roomId);
            if (rtcRoom == null) {
                log.info("can't find rtcRoom by roomId {} ,may be already close", roomId);
                return false;
            }
            return rtcRoom.isRoomActive();
        } finally {
            lock.unlock();
        }
    }
}
