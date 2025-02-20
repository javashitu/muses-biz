package com.muses.service.live.handler;

import com.muses.domain.servicce.proto.BaseReq;

/**
 * @ClassName RtcHandler
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/7 13:13
 */
public interface RtcHandler<T extends BaseReq> {
    String getType();

    void handleMsg(T request);
}
