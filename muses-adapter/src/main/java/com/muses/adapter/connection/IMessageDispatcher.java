package com.muses.adapter.connection;

import com.muses.domain.servicce.proto.BaseReq;

/**
 * @ClassName IMessageDispatcher
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/7 14:16
 */
public interface IMessageDispatcher {

    void dispatchMessage(String connectionMessage);

    void dispatchReq(BaseReq baseReq);

}
