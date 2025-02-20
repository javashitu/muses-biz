package com.muses.connection.socketio.handler;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.muses.adapter.connection.IConnectionContext;
import com.muses.adapter.connection.IMessageDispatcher;
import com.muses.common.constant.ConnectionConstant;
import com.muses.common.util.JsonFormatter;
import com.muses.connection.bo.SocketIOConnection;
import com.muses.common.constant.SocketIOEventConstant;
import com.muses.domain.servicce.proto.BaseReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName SocketMessageHandler
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/7 13:09
 */
@Slf4j
@Component

public class SocketMessageHandler {

    @Autowired
    private IConnectionContext context;

    private AtomicInteger count = new AtomicInteger(0);

    @Autowired
    private IMessageDispatcher messageDispatcher;

    @Autowired
    private JsonFormatter jsonFormatter;


    /**
     * 容器销毁前，自动调用此方法，关闭 socketIo 服务端
     *
     * @return
     * @Param []
     **/


//    @PostConstruct
//    public void init() {
//        log.debug("SocketEventListener initialized");
//
//        //添加监听，客户端自动连接到 socket 服务端
//        socketIoServer.addConnectListener(client -> {
//            String userId = client.getHandshakeData().getSingleUrlParam("userId");
//            SocketUtil.connectMap.put(userId, client);
//            log.debug("客户端userId: " + userId + "已连接，客户端ID为：" + client.getSessionId());
//        });
//
//        //添加监听，客户端跟 socket 服务端自动断开
//        socketIoServer.addDisconnectListener(client -> {
//            String userId = client.getHandshakeData().getSingleUrlParam("userId");
//            SocketUtil.connectMap.remove(userId, client);
//            log.debug("客户端userId:" + userId + "断开连接，客户端ID为：" + client.getSessionId());
//        });
//    }

    @OnConnect
    public void onConnect(SocketIOClient client) {
        String clientId = client.getHandshakeData().getSingleUrlParam(ConnectionConstant.SOCKET_IO_CLIENT_ID_MARK);
        String roomId = client.getHandshakeData().getSingleUrlParam(ConnectionConstant.SOCKET_IO_CLIENT_ROOM_ID_MARK);
        int countNum = count.getAndIncrement();
        log.info("client connected has connected client countNum {} clientId {}", countNum, clientId);
        SocketIOConnection connection = SocketIOConnection.builder()
                .connectionId(clientId)
                .client(client)
                .roomId(roomId)
                .build();
        context.onConnect(connection);
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        String clientId = client.get(ConnectionConstant.SOCKET_IO_CLIENT_ID_MARK);
        log.info("client disconnect , clientId {}", clientId);
        context.onDisconnect(clientId);
    }

    @OnEvent(value = SocketIOEventConstant.INBOUND_CHANNEL)
    public void onMessage(SocketIOClient client, AckRequest ackRequest, BaseReq message) throws Exception {
        log.info("receive message event, message {}", message);
        messageDispatcher.dispatchReq(message);
        log.info("message request deal finished");
    }

}