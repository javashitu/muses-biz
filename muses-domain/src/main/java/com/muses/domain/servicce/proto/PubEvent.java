package com.muses.domain.servicce.proto;

import com.muses.domain.servicce.enums.ProtoTypeEnums;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName PubEvent
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/19 20:07
 */
@Data
@NoArgsConstructor
public class PubEvent extends BaseEvent {

    private String userId;

    private String userName;

    private String signalType;

    private Object signalMessage;

    private Stream pubStream;

    @Builder
    public PubEvent(String userId, String userName, String signalType, Object signalMessage, Stream pubStream) {
        super(ProtoTypeEnums.PUB.getEventType());
        this.userId = userId;
        this.userName = userName;
        this.signalType = signalType;
        this.signalMessage = signalMessage;
        this.pubStream = pubStream;
    }
}
