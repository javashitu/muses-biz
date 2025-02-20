package com.muses.connection.socketio.context;

import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.google.common.collect.Lists;
import com.muses.adapter.connection.IConnectionContext;
import com.muses.common.constant.ConnectionConstant;
import com.muses.connection.socketio.handler.SocketMessageHandler;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName ServerRunner
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/7 13:06
 */

@Slf4j
@Component
@AllArgsConstructor
public class ServerRunner implements CommandLineRunner {

    private List<String> namespaceList = Lists.newArrayList("/message", "/protobuf");

    private final SocketIOServer server;

    private final SocketIONamespace messageSocketNameSpace;

    @Autowired
    private SocketMessageHandler socketMessageHandler;

    @Autowired
    private IConnectionContext connectionContext;

    @Autowired
    private ServerRunner(SocketIOServer server) {
        this.server = server;
        server.addPingListener(socketIOClient -> {
            String userId = socketIOClient.get(ConnectionConstant.SOCKET_IO_CLIENT_ID_MARK);
            log.info("client ping server user id {}", userId);
            connectionContext.onPing(userId);
        });
        //大坑，socketio高版本把ping pong改成了pong ping，客户端的心跳变成了由服务端来维护
        server.addPongListener(socketIOClient -> {
            String userId = socketIOClient.get(ConnectionConstant.SOCKET_IO_CLIENT_ID_MARK);
            log.info("server send pong to client user id {}", userId);
            connectionContext.onPing(userId);
        });
        messageSocketNameSpace = server.addNamespace(namespaceList.get(0));
    }

    @Bean(name = "messageNamespace")
    public SocketIONamespace messageNameSpace() {
        messageSocketNameSpace.addListeners(socketMessageHandler);
        return messageSocketNameSpace;
    }

    @Override
    public void run(String... args) {
        log.info("#############################");
        log.info("#                           #");
        log.info("#  ServerRunner start...    #");
        log.info("#                           #");
        log.info("#############################");

        server.start();
    }

    @PreDestroy
    private void destroy() {
        try {
            log.info("关闭 socket 服务端");
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
