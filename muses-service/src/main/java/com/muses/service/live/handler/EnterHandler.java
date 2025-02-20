package com.muses.service.live.handler;

import com.google.common.collect.Maps;
import com.muses.adapter.connection.IConnectionContext;
import com.muses.common.enums.LiveCloseReasonEnums;
import com.muses.common.enums.ServerErrorCodeEnums;
import com.muses.common.exception.ServerException;
import com.muses.common.util.MapComputeUtils;
import com.muses.domain.live.bo.*;
import com.muses.domain.servicce.enums.ProtoTypeEnums;
import com.muses.domain.servicce.proto.*;
import com.muses.service.live.config.LiveConfig;
import com.muses.service.live.context.MediaContext;
import com.muses.service.live.rtc.IRtcRoomExtender;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName EnterHandler
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/17 10:32
 */
@Slf4j
@Component
public class EnterHandler implements RtcHandler<EnterReq> {

    @Autowired
    private MediaContext mediaContext;

    @Autowired
    private IConnectionContext connectionContext;

    @Autowired
    private LiveConfig liveConfig;

    @Autowired
    private IRtcRoomExtender roomExtender;

    public String getType() {
        return ProtoTypeEnums.ENTER.getRtcProtoType();
    }

    @Override
    public void handleMsg(EnterReq request) {
        log.info("on enterHandler, user {} enter room {} ", request.getUserId(), request.getRoomId());
        if (StringUtils.isBlank(request.getRoomId())) {
            throw ServerException.builder().serverErrorCodeEnums(ServerErrorCodeEnums.PARAM_WRONG).build();
        }
        RtcRoom room = mediaContext.getRoomById(request.getRoomId());
        //TODO 有击穿的风险，需要考虑解释
        if (!checkRoomValid(room)) {
            BaseEvent closeEvent = CloseEvent.builder()
                    .rtcRoomId(request.getRoomId())
                    .reason(LiveCloseReasonEnums.NO_USER.getReason())
                    .description(LiveCloseReasonEnums.NO_USER.getDesc())
                    .build();

            log.info("room has close, user {} can't enter roomId {} ", request.getUserId(), request.getRoomId());
            connectionContext.closeById(request.getUserId(), closeEvent);
            return;
        }

        userEnterRoom(room, request);
        //感知其他都谁在房间里
        Set<String> userIdSet = room.getOtherUserIds(request.getUserId());
        responseEnter(room, request, userIdSet);
        if (CollectionUtils.isEmpty(userIdSet)) {
            return;
        }
        notifyOtherUser(request, userIdSet);
    }

    private boolean checkRoomValid(RtcRoom room) {
        return room != null && room.isRoomActive();
    }

    private void userEnterRoom(RtcRoom room, EnterReq request) {
        long timeout = room.isAnchor(request.getUserId()) ? roomExtender.getAnchorExtendTime() : roomExtender.getUserExtendTime();
        RtcUser user = RtcUser.builder()
                .id(request.getUserId())
                .name(request.getUserName())
                .timeout(timeout)
                .build();
        room.enterUser(user);

    }

    private void responseEnter(RtcRoom room, EnterReq request, Set<String> userIdSet) {
        LivePeerConfig livePeerConfig = liveConfig.getLivePeerConfig();
        Map<String, List<PubStream>> userPubStreamMap = room.getAlUserPubStream();
        Map<String, List<String>> userPubStreamIdMap = Maps.newHashMap();
        userPubStreamMap.forEach((pubUserId, pubStreamList) -> {
            if (CollectionUtils.isEmpty(pubStreamList)) {
                log.error("user not pub any stream, but record in pubStreamMap, something wrong");
                return;
            }
            pubStreamList.forEach(pubStream -> MapComputeUtils.computeMapList(userPubStreamIdMap, pubStream.getStreamId(), pubStream::getPubUserId));
        });
        //感知其他都谁在房间里
        EnterRsp enterRsp = EnterRsp.builder()
                .roomId(room.getRoomId())
                .otherUsers(userIdSet)
                .userId(request.getUserId())
                .constraints(liveConfig.getConstraints())
                .flutterRtcMediaConf(new FlutterRtcMediaConf(liveConfig.getConstraints()))
                .peerConfig(livePeerConfig)
                .userPubMap(userPubStreamIdMap)
                .anchorFlag(room.isAnchor(request.getUserId()))
                .build();
        log.info("emit to user {} enterRsp {}", request.getUserId(), enterRsp);
        connectionContext.emitRsp(request.getUserId(), enterRsp);
    }

    private void notifyOtherUser(EnterReq request, Set<String> userIdSet) {
        //这种类似事件广播的逻辑可以收敛到room模型里，但是会导致room太重
        EnterEvent enterEvent = EnterEvent.builder().userId(request.getUserId()).userName(request.getUserName()).build();
        connectionContext.broadcast(userIdSet, enterEvent);
    }
}
