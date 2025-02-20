package com.muses.service.live.handler;

import com.muses.adapter.connection.IConnectionContext;
import com.muses.domain.live.bo.SubStream;
import com.muses.domain.servicce.proto.HangupEvent;
import com.muses.domain.servicce.proto.HangupReq;
import com.muses.service.live.context.MediaContext;
import com.muses.domain.servicce.enums.ProtoTypeEnums;
import com.muses.domain.live.bo.RtcRoom;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName HangUpHandler
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/2 10:11
 * 挂断可以挂断pub，也可以挂断sub
 */
@Slf4j
@Component
public class HangupHandler implements RtcHandler<HangupReq> {

    @Autowired
    private MediaContext mediaContext;

    @Autowired
    private IConnectionContext connectionContext;

    @Override
    public String getType() {
        return ProtoTypeEnums.HANGUP.getRtcProtoType();
    }

    @Override
    public void handleMsg(HangupReq request) {
        RtcRoom room = mediaContext.getRoomById(request.getRoomId());
        if (!request.isPubStreamFlag()) {
            room.hangupSubStream(request.getStreamId());
            return;
        }
        Map<String, List<SubStream>> userSubMap = room.hangupPubStream(request.getStreamId());
        if (MapUtils.isEmpty(userSubMap)) {
            log.info("no user sub pubStream or the pubStream not fund pubStreamId {} ", request.getStreamId());
            return;
        }
        userSubMap.forEach((key, value) -> {
            HangupEvent hangupEvent = HangupEvent.builder()
                    .userId(request.getUserId())
                    .userName(request.getUserName())
                    .streamId(value.stream().map(SubStream::getStreamId).collect(Collectors.toList()))
                    .build();
            connectionContext.emitEvent(key, hangupEvent);
        });
    }

}
