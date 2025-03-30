package com.muses.service.live.handler;

import com.muses.adapter.connection.IConnectionContext;
import com.muses.adapter.service.ILiveService;
import com.muses.common.enums.LiveCloseReasonEnums;
import com.muses.domain.live.bo.RtcRoom;
import com.muses.domain.servicce.enums.ProtoTypeEnums;
import com.muses.domain.servicce.proto.CloseEvent;
import com.muses.domain.servicce.proto.CloseReq;
import com.muses.service.live.config.LiveConfig;
import com.muses.service.live.context.MediaContext;
import com.muses.service.live.rtc.IRtcRoomExtender;
import com.muses.service.live.rtc.IRtcRoomService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @ClassName CloseHandler
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2025/3/21 16:42
 */
@Slf4j
@Component
public class CloseHandler implements RtcHandler<CloseReq> {

    @Autowired
    private MediaContext mediaContext;

    @Autowired
    private IConnectionContext connectionContext;

    @Autowired
    private LiveConfig liveConfig;

    @Autowired
    private IRtcRoomExtender roomExtender;

    @Autowired
    private IRtcRoomService rtcRoomService;

    @Autowired
    private ILiveService liveService;

    public String getType() {
        return ProtoTypeEnums.CLOSE.getRtcProtoType();
    }

    @Override
    public void handleMsg(CloseReq request) {
        log.info("user {} try close the live {}", request.getUserId(), request.getRoomId());
        RtcRoom room = mediaContext.getRoomById(request.getRoomId());
        room.closeMarkRoom();
        Set<String> userIdSet = room.getAllUserId();
        if (CollectionUtils.isEmpty(userIdSet)) {
            log.info("no alive user in live room, maybe concurrent leave");
            return;
        }
        LiveCloseReasonEnums reason = LiveCloseReasonEnums.ANCHOR_CLOSE;
        CloseEvent closeEvent = CloseEvent.builder()
                .rtcRoomId(room.getRoomId())
                .reason(reason.getReason())
                .description(reason.getDesc())
                .build();
        log.info("will notify all people hang up stream and close live ");
        for (String userId : userIdSet) {
            //TODO 这里的userId代替userName要替换掉
            Set<String> mineSubbedUserSet = rtcRoomService.notifyMySubbedUser(room, userId, userId);
            //其实不通知也可以，直接要求所有收到结束直播的消息的用户销毁所有的rtc资源，包括peerConnection和connection也可以
            rtcRoomService.leaveAndNotifySubMyPeople(room, userId, userId, mineSubbedUserSet);
            connectionContext.emitEvent(userId, closeEvent);
            if (!room.hasAliveUser()) {
                mediaContext.closeRoomAfterExit(room.getRoomId());
                liveService.terminateLive(room.getRoomId(), reason);
            }
        }
        room.closeCleanRoom();
    }
}
