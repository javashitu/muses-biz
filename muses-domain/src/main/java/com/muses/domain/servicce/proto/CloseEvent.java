package com.muses.domain.servicce.proto;

import com.muses.domain.servicce.enums.ProtoTypeEnums;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName CloseEvent
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/11 13:45
 */
@Data
@NoArgsConstructor
public class CloseEvent extends BaseEvent {

    private String rtcRoomId;

    private String reason;

    private String description;

    @Builder
    public CloseEvent(String rtcRoomId, String reason, String description) {
        super(ProtoTypeEnums.CLOSE.getEventType());
        this.rtcRoomId = rtcRoomId;
        this.reason = reason;
        this.description = description;
    }
}
