package com.muses.service.live.handler;

import com.google.common.collect.Maps;
import com.muses.adapter.connection.IConnectionContext;
import com.muses.domain.live.bo.RtcRoom;
import com.muses.domain.servicce.enums.ProtoTypeEnums;
import com.muses.domain.servicce.enums.SignalTypeEnums;
import com.muses.domain.servicce.proto.NegoEvent;
import com.muses.domain.servicce.proto.NegoReq;
import com.muses.service.live.context.MediaContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName NegoHandler
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/24 11:17
 */
@Slf4j
@Component
public class NegoHandler implements RtcHandler<NegoReq> {
    @Autowired
    private MediaContext mediaContext;


    @Autowired
    private IConnectionContext connectionContext;


    @Override
    public String getType() {
        return ProtoTypeEnums.NEGO.getRtcProtoType();
    }

    @Override
    public void handleMsg(NegoReq request) {
        log.info("negotiation message from user {} in room {}, request -> {}", request.getUserId(), request.getRoomId(), request);
        RtcRoom room = mediaContext.getRoomById(request.getRoomId());
        if (room == null) {
            log.info("room is null , maybe has been closed ,roomId {}", request.getRoomId());
            return;
        }
        SignalTypeEnums signalTypeEnums = SignalTypeEnums.wrap(request.getSignalType());
        switch (signalTypeEnums) {
            case ICE:
                Map<String, String> usrStreamMap = findNotifier(request);
                notifyNego(request, SignalTypeEnums.ICE, usrStreamMap);
                break;
            case ANSWER:
                usrStreamMap = findNotifier(request);
                notifyNego(request, SignalTypeEnums.ANSWER, usrStreamMap);
                break;
            case OFFER:
                usrStreamMap = findNotifier(request);
                notifyNego(request, SignalTypeEnums.ANSWER, usrStreamMap);
                log.info("nego offer");
        }
    }

    private Map<String, String> findNotifier(NegoReq request) {
        Map<String, String> result = Maps.newHashMap();
        if (request.isNegoPubFlag()) {
            result.put(request.getSubStream().getStreamId(), request.getSubStream().getUserId());
        } else {
            result.put(request.getPubStream().getStreamId(), request.getPubStream().getUserId());
        }
        return result;

//        String pubOrSub = request.getPubOrSub();
//        if (StringUtils.equals("pub", pubOrSub)) {
//            return room.getSubUserStream(request.getUserId());
//        } else if (StringUtils.equals("sub", pubOrSub)) {
//            return room.getPubUserStream(request.getUserId());
//        } else {
//            log.warn("unknown ice type ");
//            return Collections.emptyMap();
//        }
    }

    private void notifyNego(NegoReq request, SignalTypeEnums signalTypeEnum, Map<String, String> usrStreamMap) {
        usrStreamMap.forEach((streamId, userId) -> {
            log.info("send negotiation to user {} for his stream {}", userId, streamId);
            NegoEvent negoEvent = NegoEvent.builder()
                    .signalType(signalTypeEnum.getRtcSignalType())
                    .signalMessage(request.getSignalMessage())
                    .userId(userId)
                    .userName(userId)
                    .negoPubFlag(request.isNegoPubFlag())
                    .pubStream(request.getPubStream())
                    .subStream(request.getSubStream())
                    .build();

            connectionContext.emitEvent(userId, negoEvent);
        });
    }

}
