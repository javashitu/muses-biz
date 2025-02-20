package com.muses.domain.servicce.proto;

import com.muses.domain.servicce.enums.ProtoTypeEnums;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName NegoEvent
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/24 11:12
 */
@Data
@NoArgsConstructor
public class NegoEvent extends BaseEvent {
    private String roomId;

    private String userId;

    private String userName;

    //是携带pubStream来协商还是携带subStream来协商，true携带pubStream, false携带subStream
    private boolean negoPubFlag;

    private Stream pubStream;

    private Stream subStream;

    private String signalType;

    private Object signalMessage;

    @Builder
    public NegoEvent(String roomId, String userId, String userName, boolean negoPubFlag, Stream pubStream, Stream subStream, String signalType, Object signalMessage) {
        super(ProtoTypeEnums.NEGO.getEventType());
        this.roomId = roomId;
        this.userId = userId;
        this.userName = userName;
        this.negoPubFlag = negoPubFlag;
        this.pubStream = pubStream;
        this.subStream = subStream;
        this.signalType = signalType;
        this.signalMessage = signalMessage;
    }
}
