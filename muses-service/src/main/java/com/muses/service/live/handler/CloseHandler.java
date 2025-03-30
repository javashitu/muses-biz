package com.muses.service.live.handler;

import com.muses.adapter.connection.IConnectionContext;
import com.muses.adapter.service.ILiveService;
import com.muses.domain.live.bo.RtcRoom;
import com.muses.domain.servicce.enums.ProtoTypeEnums;
import com.muses.domain.servicce.proto.CloseEvent;
import com.muses.domain.servicce.proto.CloseReq;
import com.muses.domain.servicce.proto.EnterReq;
import com.muses.service.live.config.LiveConfig;
import com.muses.service.live.context.MediaContext;
import com.muses.service.live.handler.service.ILiveRoomService;
import com.muses.service.live.rtc.IRtcRoomExtender;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
    private ILiveRoomService liveRoomService;

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
        if(CollectionUtils.isEmpty(userIdSet)){
            log.info("no alive user in live room, maybe concurrent leave");
            return;
        }
        //TODO 结束原因先置空
        CloseEvent closeEvent = CloseEvent.builder()
                .rtcRoomId(room.getRoomId())
                .reason("")
                .description("")
                .build();
        log.info("will notify all people hang up stream and close live ");
        for(String userId : userIdSet){
            //TODO 这里的userId代替userName要替换掉
            Set<String> mineSubbedUserSet = liveRoomService.notifyMySubbedUser(room, userId, userId);
            liveRoomService.leaveAndNotifySubMyPeople(room, userId, userId, mineSubbedUserSet);
            if (!room.hasAliveUser()) {
                mediaContext.closeRoomAfterExit(room.getRoomId());
                liveService.terminateLiveProgram(room.getRoomId());
            }
            connectionContext.emitEvent(userId, closeEvent);
        }
        room.closeCleanRoom();
        mediaContext.closeRoomAfterExit(room.getRoomId());
    }
}
