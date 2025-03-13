package com.muses.connection.socketio.config;

import com.corundumstudio.socketio.AuthorizationResult;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import com.muses.common.constant.ConnectionConstant;
import com.muses.common.util.TokenAuthenticator;
import io.jsonwebtoken.Claims;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @ClassName SocketIOConfig
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/7 13:02
 */
@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "app.socketio")
public class SocketIOConfig {

    private String host;

    private Integer port;

    private String externalHost;

    private int bossCount;

    private int workCount;

    private boolean allowCustomRequests;

    private int upgradeTimeout;

    private int pingTimeout;

    private int pingInterval;


    @Bean
    public SocketIOServer socketIOServer(@Autowired TokenAuthenticator tokenAuthenticator) {
        Configuration configuration = new Configuration();
        configuration.setPort(port);
        if (StringUtils.isNotBlank(host) && !StringUtils.equals("0.0.0.0", host)) {
            configuration.setHostname(host);
        }

        SocketConfig socketConfig = new SocketConfig();
        socketConfig.setReuseAddress(true);
        configuration.setSocketConfig(socketConfig);
        configuration.setOrigin(null);
        configuration.setBossThreads(bossCount);
        configuration.setWorkerThreads(workCount);
        configuration.setAllowCustomRequests(allowCustomRequests);

        configuration.setUpgradeTimeout(upgradeTimeout);
        configuration.setPingTimeout(pingTimeout);
        configuration.setPingInterval(pingInterval);
        //设置 sessionId 随机
        configuration.setRandomSession(true);

        configuration.setAuthorizationListener(data -> {
            String token = data.getSingleUrlParam(ConnectionConstant.SOCKET_IO_URL_TOKEN_QUERY);
            String rtcUserId = data.getSingleUrlParam(ConnectionConstant.SOCKET_IO_URL_CONNECTION_ID_QUERY);
            log.info("client connect to server begin token auth , connectionId {} ,token {}", rtcUserId, token);
            Pair<Boolean, Claims> pair = tokenAuthenticator.validToken(token, rtcUserId);
            if (Boolean.FALSE.equals(pair.getLeft())) {
//                return false;
//                throw new RuntimeException("auth token failure");
                return AuthorizationResult.FAILED_AUTHORIZATION;
            }
            Claims claims = pair.getRight();
//            return tokenAuthenticator.isExpire(claims);
            if(tokenAuthenticator.isExpire(claims)){
                return AuthorizationResult.FAILED_AUTHORIZATION;
//                throw new RuntimeException("auth token failure");
            }
            return AuthorizationResult.SUCCESSFUL_AUTHORIZATION;
        });

        //初始化 Socket 服务端配置
        return new SocketIOServer(configuration);
    }

    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketIOServer) {
        return new SpringAnnotationScanner(socketIOServer);
    }
}
