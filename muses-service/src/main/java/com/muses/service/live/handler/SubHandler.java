package com.muses.service.live.handler;

import com.muses.adapter.connection.IConnectionContext;
import com.muses.domain.live.bo.RtcRoom;
import com.muses.domain.live.bo.RtcUser;
import com.muses.domain.live.bo.SubStream;
import com.muses.domain.servicce.enums.ProtoTypeEnums;
import com.muses.domain.servicce.enums.SignalTypeEnums;
import com.muses.domain.servicce.proto.Stream;
import com.muses.domain.servicce.proto.SubEvent;
import com.muses.domain.servicce.proto.SubReq;
import com.muses.service.live.context.MediaContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName MessageHandler
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/17 22:36
 */
@Slf4j
@Component
public class SubHandler implements RtcHandler<SubReq> {

    @Autowired
    private MediaContext mediaContext;

    @Autowired
    private IConnectionContext connectionContext;

    @Override
    public String getType() {
        return ProtoTypeEnums.SUB.getRtcProtoType();
    }

    @Override
    public void handleMsg(SubReq request) {
        log.info("user {} do subHandler subStreamId {}, it will sub -> {} ,the signalType {}", request.getUserId(), request.getSubStream().getStreamId(), request.getPubStream().getStreamId(), request.getSignalType());
        if (StringUtils.isEmpty(request.getSignalType())) {
            //如果不携带信令，就走通知主播发起offer,指定某个人去订阅的逻辑，这里临时生成一个SubStream,不发布
            SubStream subStream = genSubStream(request);
            notifyPub(request, subStream);
            log.info("no signal carry on, notify anchor to create a new rtcPeerConnection anchor {}", request.getPubStream().getUserId());
            return;
        }
        SignalTypeEnums signalTypeEnums = SignalTypeEnums.wrap(request.getSignalType());
        switch (signalTypeEnums) {
            case ANSWER:
                notifyPub(request, doPubSubStream(request));
                break;
            case OFFER:
                notifySub(request, genSubStream(request));
                break;
            default:
                log.error("unknown signal type");
        }
    }

    private void notifyPub(SubReq request, SubStream subStream) {
        log.info("notify the user {} ,another user  {} sub him ", subStream.getSubTargetUserId(), subStream.getUserId());
        notifyUserSubEvent(request.getPubStream().getUserId(), request, subStream);
    }

    private void notifySub(SubReq request, SubStream subStream) {
        log.info("notify the user {} do sub the stream {}", subStream.getUserId(), request.getPubStream().getStreamId());
        notifyUserSubEvent(request.getSubStream().getUserId(), request, subStream);
    }

    private void notifyUserSubEvent(String notifyUserId, SubReq request, SubStream subStream) {
        log.info("notify user {} the subEvent, and carry signal {}", notifyUserId, request.getSignalType());
        RtcRoom room = mediaContext.getRoomById(request.getRoomId());
        RtcUser rtcUser = room.getUser(request.getUserId());

        Stream sendSubStream = Stream.builder()
                .streamId(subStream.getStreamId())
                .userId(subStream.getUserId())
                .pubFlag(false)
                .build();

        Stream sendPubStream = request.getPubStream();

        SubEvent subEvent = SubEvent.builder()
                .userId(request.getUserId())
                .userName(rtcUser.getName())
                .subStream(sendSubStream)
                .pubStream(sendPubStream)
                .signalType(request.getSignalType())
                .signalMessage(request.getSignalMessage())
                .build();

        connectionContext.emitEvent(notifyUserId, subEvent);
    }


    private SubStream doPubSubStream(SubReq request) {
        RtcRoom room = mediaContext.getRoomById(request.getRoomId());
        SubStream subStream = genSubStream(request);
        room.doPubSubStream(subStream);
        return subStream;
    }

    private SubStream genSubStream(SubReq request) {
        return SubStream.builder()
                .userId(request.getSubStream().getUserId())
                .streamId(request.getSubStream().getStreamId())
                .subTargetStreamId(request.getPubStream().getStreamId())
                .subTargetUserId(request.getPubStream().getUserId())
                .build();
    }

}
