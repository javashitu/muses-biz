package com.muses.domain.servicce.proto;

import com.muses.domain.servicce.enums.ProtoTypeEnums;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName HngupEvent
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/2 16:39
 */
@Data
@NoArgsConstructor
public class HangupEvent extends BaseEvent{

    private String userId;

    private String userName;

    private List<String> streamId;

    private String hangupReason;

    @Builder
    public HangupEvent(String userId, String userName, List<String> streamId, String hangupReason) {
        super(ProtoTypeEnums.HANGUP.getEventType());
        this.userId = userId;
        this.userName = userName;
        this.streamId = streamId;
        this.hangupReason = hangupReason;
    }
}
