package com.muses.domain.servicce.proto;

import com.muses.domain.servicce.enums.ProtoTypeEnums;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName LeaveEvent
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/2 17:33
 */
@Data
@NoArgsConstructor
public class LeaveEvent extends BaseEvent{

    private String userId;

    private String userName;

    //如果挂断流，一定要挂断对应的peerConnection
    private List<String> hangUpStreamId;

    //可以只挂断peerConnection，因为可能不存在对应的流
    private List<String> closePeerConnectionList;

    @Builder
    public LeaveEvent(String protoType, String userId, String userName, List<String> hangUpStreamId, List<String> closePeerConnectionList) {
        super(ProtoTypeEnums.LEAVE.getEventType());
        this.userId = userId;
        this.userName = userName;
        this.hangUpStreamId = hangUpStreamId;
        this.closePeerConnectionList = closePeerConnectionList;
    }
}
