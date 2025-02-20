package com.muses.service.live.handler;

import com.muses.adapter.connection.IConnectionContext;
import com.muses.domain.live.bo.PubStream;
import com.muses.domain.live.bo.RtcRoom;
import com.muses.domain.live.bo.RtcUser;
import com.muses.domain.servicce.enums.ProtoTypeEnums;
import com.muses.domain.servicce.enums.SignalTypeEnums;
import com.muses.domain.servicce.proto.PubEvent;
import com.muses.domain.servicce.proto.PubReq;
import com.muses.domain.servicce.proto.PubRsp;
import com.muses.domain.servicce.proto.Stream;
import com.muses.service.live.context.MediaContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName PubHandler
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/18 6:05
 * 1v1 通话，主叫发起offer,被叫回应answer,之后各个端沟通
 * 1V多通话， 主叫发布视频，被叫感知到主叫发布视频后，主动发起offer通知主叫，主叫回复answer
 * 这种方式下如果被叫不打开摄像头直接offer,主叫收到offer后发起answer然后就不会产生任何行令了，卡在了ice步骤
 * 如果被叫也打开摄像头，然后发offer,虽然最后的协商里也收到了ice,并且感知到了对方的流准备就绪，但是没有收到推流的，被叫的视频播放没打开。
 * <p>
 * 1V多通话，被叫发起订阅，然后通知主叫创建新的peerConnectin，然后主叫发offer到被叫。
 */
@Slf4j
@Component
public class PubHandler implements RtcHandler<PubReq> {

    @Autowired
    private MediaContext mediaContext;

    @Autowired
    private IConnectionContext connectionContext;

    @Override
    public String getType() {
        return ProtoTypeEnums.PUB.getRtcProtoType();
    }

    @Override
    public void handleMsg(PubReq request) {
        log.info("user {} pub stream in room {} , pub streamId {}", request.getUserId(), request.getRoomId(), request.getPubStream().getStreamId());
        RtcRoom room = mediaContext.getRoomById(request.getRoomId());
        if (room == null) {
            log.info("room is null , maybe has been closed ,roomId {}", request.getRoomId());
            return;
        }
        SignalTypeEnums signalTypeEnums = SignalTypeEnums.wrap(request.getSignalType());
        switch (signalTypeEnums) {
            case OFFER:
                PubStream pubStream = pubStream(room, request);
                notifyPub(room, request, pubStream);
                break;
            default:
                log.error("unknown signal type,maybe rtc version is wrong");
        }

    }

    /**
     * 发布流的逻辑放在服务端更好控制，比如被禁止pub时，需要在服务端判断完毕，client才能知道能不能pub
     */
    private PubStream pubStream(RtcRoom room, PubReq request) {
        //TODO 怎么识别出来是发布的什么流
        PubStream pubStream = PubStream.builder()
                .streamId(request.getPubStream().getStreamId())
                .audioFlag(true)
                .videoFlag(true)
                .shareDeskFlag(true)
                .pubUserId(request.getUserId())
                .build();
        room.pubStream(pubStream);
        return pubStream;
    }

    private void notifyPub(RtcRoom room, PubReq request, PubStream pubStream) {
        RtcUser rtcUser = room.getUser(request.getUserId());

        Stream stream = Stream.builder()
                .streamId(pubStream.getStreamId())
                .userId(pubStream.getPubUserId())
                .pubFlag(true)
                .audio(pubStream.isVideoFlag())
                .video(pubStream.isAudioFlag())
                .build();

        PubRsp pubRsp = PubRsp.builder()
                .roomId(room.getRoomId())
                .userId(rtcUser.getId())
                .pubStream(stream)
                .build();
        //流发布成功后还要通知到自己，这里不要复用event，否则本人会收到本人的offer
        connectionContext.emitRsp(rtcUser.getId(), pubRsp);

        PubEvent pubEvent = PubEvent.builder()
                .userId(request.getUserId())
                .userName(rtcUser.getName())
                .pubStream(stream)
                .signalType(SignalTypeEnums.OFFER.getRtcSignalType())
                .signalMessage(request.getSignalMessage())
                .build();
        log.info("notify other people the {} is pub stream {} ", request.getUserId(), pubStream.getStreamId());
        mediaContext.boardcastExclusion(request.getRoomId(), request.getUserId(), pubEvent);
    }

}
