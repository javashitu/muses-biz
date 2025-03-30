package com.muses.service.live.handler;

import com.muses.adapter.connection.IConnectionContext;
import com.muses.adapter.service.ILiveService;
import com.muses.common.enums.ServerErrorCodeEnums;
import com.muses.common.exception.ServerException;
import com.muses.common.util.JsonFormatter;
import com.muses.domain.live.bo.RtcRoom;
import com.muses.domain.live.bo.SubStream;
import com.muses.domain.servicce.enums.ProtoTypeEnums;
import com.muses.domain.servicce.proto.LeaveEvent;
import com.muses.domain.servicce.proto.LeaveReq;
import com.muses.service.live.context.MediaContext;
import com.muses.service.live.handler.service.ILiveRoomService;
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

    @Autowired
    private ILiveRoomService liveRoomService;

    @Autowired
    private ILiveService liveService;

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
        Set<String> mineSubbedUserSet = liveRoomService.notifyMySubbedUser(room, request.getUserId(), request.getUserName());
        liveRoomService.leaveAndNotifySubMyPeople(room, request.getUserId(), request.getUserName(), mineSubbedUserSet);
        if (!room.hasAliveUser()) {
            mediaContext.closeRoomAfterExit(room.getRoomId());
            liveService.terminateLiveProgram(room.getRoomId());
        }
    }


}
