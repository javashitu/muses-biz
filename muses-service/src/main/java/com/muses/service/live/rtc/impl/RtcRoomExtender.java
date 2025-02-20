package com.muses.service.live.rtc.impl;

import com.muses.adapter.connection.IConnectionContext;
import com.muses.adapter.service.ILiveService;
import com.muses.common.constant.LiveConstant;
import com.muses.common.enums.LiveCloseReasonEnums;
import com.muses.common.enums.UserExpelReasonEnums;
import com.muses.common.util.DateTimeUtils;
import com.muses.common.util.JsonFormatter;
import com.muses.common.util.iface.IDistributeLock;
import com.muses.domain.live.bo.RtcRoom;
import com.muses.domain.live.bo.RtcUser;
import com.muses.domain.servicce.proto.CloseEvent;
import com.muses.domain.servicce.proto.ExpelEvent;
import com.muses.persistence.mysql.service.ILiveProgramRepoService;
import com.muses.persistence.redis.common.RedisFormatter;
import com.muses.service.live.config.LiveConfig;
import com.muses.service.live.context.IMediaContext;
import com.muses.service.live.rtc.IRtcRoomExtender;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @ClassName RtcRoomExtensionService
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/10 16:08
 */
@Slf4j
@Component
public class RtcRoomExtender implements IRtcRoomExtender {

    @Autowired
    private LiveConfig liveConfig;

    @Autowired
    private RedisFormatter redisFormatter;

    @Autowired
    private ILiveProgramRepoService liveProgramRepoService;

    @Autowired
    private JsonFormatter jsonFormatter;

    @Autowired
    private ILiveService liveService;

    @Autowired
    private IMediaContext mediaContext;

    @Autowired
    private IConnectionContext connectionContext;

    @Autowired
    private IDistributeLock distributeLock;

    public long getInitRoomExpireTime() {
        return DateTimeUtils.currentTime() + liveConfig.getRoomExtendTime();
    }

    @Override
    public boolean initRoomExtender(RtcRoom room) {
        String roomId = room.getRoomId();
        long initTime = room.getExpireTime();
        log.info("init room expire time for room {} in time {}", roomId, initTime);
        //兜底，保证服务挂掉后不会产生僵尸直播间
        return redisFormatter.zadd(LiveConstant.ACTIVE_LIVE_SET_NAME, roomId, initTime);
    }

    @Override
    public void scanExpireRoom() {
        long curTime = DateTimeUtils.currentTime();
        mediaContext.getAllRoom().forEach((roomId, room) -> {
            if (room.isRoomExpireInTime(curTime)) {
                log.info("rtc room timeout , will close this room {}", roomId);
                CloseEvent closeEvent = CloseEvent.builder()
                        .rtcRoomId(roomId)
                        .reason(LiveCloseReasonEnums.NO_ANCHOR.getReason())
                        .description(LiveCloseReasonEnums.NO_ANCHOR.getDesc()).build();
                mediaContext.closeRoomForce(roomId, closeEvent);
            }
        });

    }

    /**
     * 这个扫描间隔应该小于一般情况下的服务器重启时间，如果重启后room还没过期，是不能扫描出过期的room的
     * 只能等待实际room过期后才能把这个直播间关闭，这期间有可能会创建出已经被销毁的直播间的连接url导致无法连接
     */
    @Override
    public void scanExpireRoomInRedis() {
        long curTime = DateTimeUtils.currentTime();
        Map<String, Double> expireRoomMap = redisFormatter.zrangebyscore(LiveConstant.ACTIVE_LIVE_SET_NAME, 0, curTime);

        log.info("scan expired room {} in time {} ", jsonFormatter.object2Json(expireRoomMap.keySet()), DateTimeUtils.formatAsDate(curTime));
        if (MapUtils.isEmpty(expireRoomMap)) {
            log.info("no expire room in time {}", DateTimeUtils.formatAsDate(curTime));
            return;
        }
        expireRoomMap.forEach((key, value) -> {
            log.info("room has expired, will be close , roomId {} expire in time {}", key, value.longValue());
            liveService.terminateLive(key);
        });
    }

    @Override
    public boolean removeRomeExtender(String roomId) {
        long removeCount = redisFormatter.zrem(LiveConstant.ACTIVE_LIVE_SET_NAME, roomId);
        return removeCount == 1L;
    }

    @Override
    public void extendRoomAndUser(String roomId, String userId) {
        RtcRoom rtcRoom = mediaContext.getRoomById(roomId);
        if (rtcRoom == null) {
            //关闭直播和心跳并发，连接还在，room没了，直接断开连接
            CloseEvent closeEvent = CloseEvent.builder()
                    .rtcRoomId(roomId)
                    .reason(LiveCloseReasonEnums.NO_ROOM.getReason())
                    .description(LiveCloseReasonEnums.NO_ROOM.getDesc()).build();
            connectionContext.closeById(userId, closeEvent);
            log.info("can't find room for room id {} ,maybe room has close, won't extend by user {} ", roomId, userId);
            return;
        }
        if (!rtcRoom.isRoomActive()) {
            //关闭直播和心跳并发，连接还在，room没了，直接断开连接
            CloseEvent closeEvent = CloseEvent.builder()
                    .rtcRoomId(roomId)
                    .reason(LiveCloseReasonEnums.ANCHOR_CLOSE.getReason())
                    .description(LiveCloseReasonEnums.ANCHOR_CLOSE.getDesc()).build();
            connectionContext.closeById(userId, closeEvent);

            log.info("room has close can't extend time user id {} -> room id {} ", userId, roomId);
            return;
        }
        long userExtendTime;
        if (rtcRoom.isAnchor(userId)) {
            long roomExtendTime = getRoomExtendTime();
            userExtendTime = getAnchorExtendTime();
            log.info("receive anchor's heart beat, extend room terminate time, anchor {} extend room time to {} anchor time to {}", userId, roomExtendTime, userExtendTime);

            mediaContext.extendRoomTimeOut(rtcRoom.getRoomId(), roomExtendTime);
//            rtcRoom.extendTimeout(roomExtendTime);
            redisFormatter.zadd(LiveConstant.ACTIVE_LIVE_SET_NAME, roomId, roomExtendTime);
        } else {
            userExtendTime = getUserExtendTime();
            log.info("receive user's heart beat, extend user timeout time, user {} extend time to {}", userId, userExtendTime);
        }
        RtcUser rtcUser = rtcRoom.getUser(userId);
        if (rtcUser != null) {
            //连接刚建立时收到的ping，此时还没把user加入到room
            rtcUser.extendTimeout(userExtendTime);
        }
    }


    @Override
    public void scanExpireUser() {
        mediaContext.getAllRoom().forEach((roomId, room) -> {
            List<RtcUser> userList = room.getExpireUser();
            if (CollectionUtils.isEmpty(userList)) {
                log.info("no rtc user timeout , ignore this timeout scan in room {}", roomId);
                return;
            }

            ExpelEvent expelEvent = ExpelEvent.builder()
                    .rtcRoomId(roomId)
                    .reason(UserExpelReasonEnums.TIMEOUT.getReason())
                    .description(UserExpelReasonEnums.TIMEOUT.getDesc()).build();

            userList.forEach(rtcUser -> {
                log.info("user has expire, remove the user and close connection, user {} expire in room {}", rtcUser.getId(), roomId);
                expelEvent.setRtcUserId(rtcUser.getId());
                mediaContext.expelUser(roomId, rtcUser.getId(), expelEvent);
            });
        });
    }

    public long getRoomExtendTime() {
        return DateTimeUtils.currentTime() + liveConfig.getRoomExtendTime();
    }

    public long getAnchorExtendTime() {
        return DateTimeUtils.currentTime() + liveConfig.getAnchorExtendTime();
    }

    public long getUserExtendTime() {
        return DateTimeUtils.currentTime() + liveConfig.getAudienceExtendTime();
    }

}
