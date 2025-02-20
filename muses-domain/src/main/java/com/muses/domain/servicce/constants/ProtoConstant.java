package com.muses.domain.servicce.constants;

/**
 * @ClassName ProtoConstantss
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/7 13:18
 */
public class ProtoConstant {

    /**
     * 不传递给client，仅作为服务端分发事件
     */
    public static final String DISPATCH = "dispatch";

    public static final String PROTO_ENTER = "enter";

    public static final String PROTO_PUB = "pub";

    public static final String PROTO_SUB = "sub";

    /**
     * negotiation
     */
    public static final String PROTO_NEGO = "nego";

    public static final String PROTO_HANGUP = "hangup";

    public static final String PROTO_LEAVE = "leave";

    public static final String PROTO_CLOSE = "close";

    public static final String PROTO_EXPEL = "expel";

}
