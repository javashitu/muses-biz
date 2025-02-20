package com.muses.adapter.connection;

import com.muses.domain.servicce.proto.BaseEvent;
import com.muses.domain.servicce.proto.BaseRsp;

import java.util.Set;

/**
 * @ClassName IConnectionMsgProcessor
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/11 10:57
 */
public interface IConnectionMsgProcessor {

    void emitRsp(String connectionId, BaseRsp baseRsp);

    void emitEvent(String connectionId, BaseEvent baseEvent);

    void broadcast(Set<String> connectionIdSet, BaseEvent baseEvent);

}
