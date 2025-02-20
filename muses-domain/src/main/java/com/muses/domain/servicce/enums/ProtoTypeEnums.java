package com.muses.domain.servicce.enums;

import com.muses.domain.servicce.constants.ProtoConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName ProtoTypeEnums
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/18 16:51
 */
@Getter
@AllArgsConstructor
public enum ProtoTypeEnums {
    DISPATCH(ProtoConstant.DISPATCH,"dispatch", "dispatch"),
    ENTER(ProtoConstant.PROTO_ENTER,"enterRsp", "enterEvent"),
    PUB(ProtoConstant.PROTO_PUB, "pubRsp", "pubEvent"),
    SUB(ProtoConstant.PROTO_SUB, "subRsp", "subEvent"),
    NEGO(ProtoConstant.PROTO_NEGO, "negoRsp", "negoEvent"),
    HANGUP(ProtoConstant.PROTO_HANGUP, "hangupRsp", "hangupEvent"),
    LEAVE(ProtoConstant.PROTO_LEAVE, "leaveRsp", "leaveEvent"),
    CLOSE(ProtoConstant.PROTO_CLOSE, "closeRsp", "closeEvent"),
    EXPEL(ProtoConstant.PROTO_EXPEL, "expelRsp", "expelEvent"),

    ;

    /**
     * 可能有的事件和应答不会成对出现，但还是定义出来每一对事件和应答
     */
    private final String rtcProtoType;

    private final String rspType;

    private final String eventType;
}
