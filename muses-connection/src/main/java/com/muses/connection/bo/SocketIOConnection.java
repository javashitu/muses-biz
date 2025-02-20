package com.muses.connection.bo;

import com.corundumstudio.socketio.SocketIOClient;
import com.muses.common.constant.ConnectionConstant;
import com.muses.common.constant.SocketIOEventConstant;
import com.muses.domain.live.bo.BaseConnection;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName SocketIOConnection
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/11 10:34
 */
@Data
@Slf4j
public class SocketIOConnection extends BaseConnection {

    private SocketIOClient client;

    @Builder
    public SocketIOConnection(String connectionId, String roomId, SocketIOClient client) {
        super(connectionId, roomId);
        this.client = client;

        client.set(ConnectionConstant.SOCKET_IO_CLIENT_ID_MARK, connectionId);
        client.set(ConnectionConstant.SOCKET_IO_CLIENT_ROOM_ID_MARK, roomId);
        client.set(ConnectionConstant.SOCKET_IO_BIND_CONNECTION_MARK, this);
    }

    @Override
    public void sendMsg(Object msg) {
        log.info("base connection send message to client message {}", msg);
        client.sendEvent(SocketIOEventConstant.OUTBOUND_CHANNEL, msg);
    }

    @Override
    public void close() {
        log.info("base connection close socketIO client {} in room {}", connectionId, roomId);
        client.del(ConnectionConstant.SOCKET_IO_BIND_CONNECTION_MARK);
        client.disconnect();
    }
}
