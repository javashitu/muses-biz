package com.muses.adapter.connection;

import com.muses.domain.live.bo.BaseConnection;
import com.muses.domain.servicce.proto.BaseEvent;

import java.util.Collection;

/**
 * @ClassName IConnectionActionTrigger
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/11 10:58
 */
public interface IConnectionActionTrigger {
    void onConnect(BaseConnection connection);

    void onDisconnect(String connectionId);

    void onPing(String connectionId);

    void closeById(String id, BaseEvent baseEvent);

    void closeByIdSet(Collection<String> connectionIdSet, BaseEvent baseEvent);
}
