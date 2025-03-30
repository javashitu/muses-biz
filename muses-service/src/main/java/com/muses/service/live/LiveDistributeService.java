package com.muses.service.live;

import com.muses.domain.live.bo.RtcRoom;
import com.muses.service.live.context.IMediaContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName LiveDistributeService
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2025/3/27 15:30
 * 校验在分布式环境下的各种直播信息对不对
 */
@Slf4j
@Component
public class LiveDistributeService {


    @Autowired
    private IMediaContext mediaContext;

    /**
     * 先不管分布式环境下怎么搞，单机实现以下
     *
     * @param liveRoomId
     * @return
     */
    public boolean isLiveActive(String liveRoomId) {
        RtcRoom rtcRoom = mediaContext.getRoomById(liveRoomId);
        return rtcRoom != null && rtcRoom.isRoomActive();
    }
}
