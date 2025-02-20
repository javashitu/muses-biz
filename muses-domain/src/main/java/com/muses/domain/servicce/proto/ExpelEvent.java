package com.muses.domain.servicce.proto;

import com.muses.domain.servicce.enums.ProtoTypeEnums;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ExpelEvent
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/13 17:22
 */
@Data
@NoArgsConstructor
public class ExpelEvent extends BaseEvent {

    private String rtcRoomId;

    private String rtcUserId;

    private String reason;

    private String description;

    @Builder
    public ExpelEvent(String rtcRoomId, String rtcUserId, String reason, String description) {
        super(ProtoTypeEnums.EXPEL.getEventType());

        this.rtcRoomId = rtcRoomId;
        this.rtcUserId = rtcUserId;
        this.reason = reason;
        this.description = description;
    }
}
