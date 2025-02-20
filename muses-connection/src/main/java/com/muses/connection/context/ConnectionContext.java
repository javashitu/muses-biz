package com.muses.connection.context;

import com.google.common.collect.Maps;
import com.muses.adapter.connection.IConnectionActionDispatcher;
import com.muses.adapter.connection.IConnectionContext;
import com.muses.common.constant.LiveConstant;
import com.muses.common.enums.ConnectionActionEnums;
import com.muses.common.util.TokenAuthenticator;
import com.muses.connection.socketio.config.SocketIOConfig;
import com.muses.domain.live.bo.BaseConnection;
import com.muses.domain.live.bo.LiveAuthInfo;
import com.muses.domain.servicce.proto.BaseEvent;
import com.muses.domain.servicce.proto.BaseRsp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName ConnectionContext
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/11 10:43
 */
@Slf4j
@Component
public class ConnectionContext implements IConnectionContext {


    private final ConcurrentHashMap<String, BaseConnection> mapClients = new ConcurrentHashMap<>();

    @Autowired
    private IConnectionActionDispatcher connectionActionDispatcher;

    @Autowired
    private SocketIOConfig socketIOConfig;

    @Autowired
    private TokenAuthenticator authenticator;


    @Override
    public void onConnect(BaseConnection connection) {
        String connectionId = connection.getConnectionId();
        log.info("onConnect -> connectionId {} ", connectionId);

        mapClients.put(connectionId, connection);
    }

    @Override
    public void onDisconnect(String connectionId) {
        log.info("onDisconnect, connection id {}", connectionId);
        BaseConnection baseConnection = mapClients.get(connectionId);
        if (baseConnection == null) {
            //服务端先直播间，关闭所有base connection, 然后底层连接断开时还会收到这个事件
            //连接刚建立，然后马上断开，也会收到这个事件
            log.info("can't find base connection , may be connection has close or base connection not init ");
            return;
        }
        connectionActionDispatcher.dispatchAction(baseConnection, ConnectionActionEnums.DISCONNECT_ACTION);
    }

    @Override
    public void onPing(String connectionId) {
        log.info("receive connection's ping message , connection id {}", connectionId);
        BaseConnection baseConnection = mapClients.get(connectionId);
        if (baseConnection == null) {
            log.info("on ping , can't find base connection, maybe base connection has been close, won't update expire time");
            return;
        }
        connectionActionDispatcher.dispatchAction(baseConnection, ConnectionActionEnums.PING_ACTION);
    }

    @Override
    public Map<String, String> getConnectionUrl(String connectionId, String roomId) {
        LiveAuthInfo liveAuthInfo = LiveAuthInfo.builder()
                .userId(connectionId)
                .roomId(roomId)
                .build();
        String token = authenticator.createToken(liveAuthInfo);
        String httpUrl = String.format(LiveConstant.SOCKET_IO_HTTP_URL, socketIOConfig.getHost(), socketIOConfig.getPort());
        String query = String.format(LiveConstant.SOCKET_IO_QUERY, connectionId, roomId, token);
        Map<String, String> map = Maps.newHashMap();
        map.put("url", httpUrl);
        map.put("query", query);
        map.put("connectionId", connectionId);
        map.put("token", token);
        map.put("roomId", roomId);
        return map;
    }

    private void emitMessage(String connectionId, Object message) {
        BaseConnection client = mapClients.get(connectionId);
        if (client != null) {
            client.sendMsg(message);
            return;
        }
        log.info("client is null, maybe has been destroy");
    }

    @Override
    public void emitRsp(String connectionId, BaseRsp baseRsp) {
        emitMessage(connectionId, baseRsp);
    }

    @Override
    public void emitEvent(String connectionId, BaseEvent baseEvent) {
        emitMessage(connectionId, baseEvent);
    }

    @Override
    public void broadcast(Set<String> connectionIdSet, BaseEvent baseEvent) {
        if (CollectionUtils.isEmpty(connectionIdSet)) {
            log.error("connectionIdSet is null, can't do broadcast");
            return;
        }
        connectionIdSet.forEach(userId -> emitMessage(userId, baseEvent));
    }

    @Override
    public void closeByIdSet(Collection<String> connectionIdSet, BaseEvent baseEvent) {
        if (CollectionUtils.isEmpty(connectionIdSet)) {
            log.error("connectionIdSet is null, ignore this close");
            return;
        }
        connectionIdSet.forEach(connectionId -> closeById(connectionId, baseEvent));
    }

    @Override
    public void closeById(String connectionId, BaseEvent baseEvent) {
        BaseConnection client = mapClients.remove(connectionId);
        if (client == null) {
            log.info("no connection in server, maybe this client has been close , connection id {}", connectionId);
            return;
        }
        if (baseEvent != null) {
            client.sendMsg(baseEvent);
        }
        client.close();
    }
}
