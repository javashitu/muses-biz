package com.muses.domain.servicce.proto;

import com.muses.domain.servicce.enums.ProtoTypeEnums;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName SubEvent
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/20 22:39
 */
@Data
@NoArgsConstructor
public class SubEvent extends BaseEvent {

    //表示发起sub的人的userId
    private String userId;

    private String userName;

    private Stream subStream;

    private Stream pubStream;

    private String signalType;

    private Object signalMessage;

    @Builder
    public SubEvent(String userId, String userName, Stream subStream, Stream pubStream, String signalType, Object signalMessage) {
        super(ProtoTypeEnums.SUB.getEventType());
        this.userId = userId;
        this.userName = userName;
        this.subStream = subStream;
        this.pubStream = pubStream;
        this.signalType = signalType;
        this.signalMessage = signalMessage;
    }
}
