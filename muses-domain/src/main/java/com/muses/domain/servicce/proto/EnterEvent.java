package com.muses.domain.servicce.proto;

import com.muses.domain.servicce.enums.ProtoTypeEnums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName EnterEvent
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/18 16:22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnterEvent extends BaseEvent{

    private String userId;

    private String userName;

    @Builder
    public EnterEvent(String protoType, String userId, String userName) {
        super(ProtoTypeEnums.ENTER.getEventType());
        this.userId = userId;
        this.userName = userName;
    }
}
