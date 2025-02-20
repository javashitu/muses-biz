package com.muses.service.live.handler;

import com.muses.adapter.connection.IConnectionActionDispatcher;
import com.muses.common.enums.ConnectionActionEnums;
import com.muses.domain.live.bo.BaseConnection;
import com.muses.service.live.context.MediaContext;
import com.muses.service.live.rtc.IRtcRoomExtender;
import com.muses.service.live.rtc.IRtcRoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName ConnectionActionHandler
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/11 15:56
 */
@Slf4j
@Component
public class ConnectionActionHandler implements IConnectionActionDispatcher {

    @Autowired
    private IRtcRoomExtender rtcRoomExtender;

    @Autowired
    private MediaContext mediaContext;

    @Autowired
    private IRtcRoomService rtcRoomService;


    @Override
    public void dispatchAction(BaseConnection connection, ConnectionActionEnums actionEnum) {
        switch (actionEnum) {
            case PING_ACTION -> rtcRoomExtender.extendRoomAndUser(connection.getRoomId(), connection.getConnectionId());
            case DISCONNECT_ACTION -> rtcRoomService.expelUser(connection.getRoomId(), connection.getConnectionId());
            default -> log.info("ignore this action, not need process ");
        }
    }
}
