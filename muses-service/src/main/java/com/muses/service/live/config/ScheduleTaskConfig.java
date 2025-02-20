package com.muses.service.live.config;

import com.muses.common.enums.ServerErrorCodeEnums;
import com.muses.common.exception.ServerException;
import com.muses.common.util.DateTimeUtils;
import com.muses.service.live.rtc.IRtcRoomExtender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @ClassName ScheduleTask
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/10 18:46
 */
@Slf4j
@Component
public class ScheduleTaskConfig {

    @Autowired
    private IRtcRoomExtender rtcRoomExtensionService;

    @Scheduled(cron = "0/30 * * * * ?")
    public void scanExpireRtcRoomInRedis() {
        long delay = 10000 + (long) (new Random().nextInt(100) * 200);
        try {
            log.info("scan expire live by redis in time {} ,the scan task will delay {} millions ", DateTimeUtils.getNowDateTime(), delay);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            log.error("scan expire live by redis failure, scan task been interrupted ,the delay time {}", delay, e);
            throw ServerException.builder().serverErrorCodeEnums(ServerErrorCodeEnums.SERVER_ERROR).cause(e).build();
        }
        log.info("begin scan redis's expired rtc room ,the delay time {} ", delay);
        rtcRoomExtensionService.scanExpireRoomInRedis();
    }

    @Scheduled(cron = "0/10 * * * * ?")
    public void scanExpireRtcRoom() {
        long delay = 1000 + (long) (new Random().nextInt(10) * 300);
        try {
            log.info("scan expire live in time {} ,the scan task will delay {} millions ", DateTimeUtils.getNowDateTime(), delay);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            log.error("scan expire live failure, scan task been interrupted ,the delay time {}", delay, e);
            throw ServerException.builder().serverErrorCodeEnums(ServerErrorCodeEnums.SERVER_ERROR).cause(e).build();
        }
        log.info("begin scan expired rtc room ,the delay time {} ", delay);
        rtcRoomExtensionService.scanExpireRoom();
    }

    @Scheduled(cron = "0/5 * * * * ?")
    public void scanTimeOutUser() {
        log.info("begin scan time out user");
        rtcRoomExtensionService.scanExpireUser();
    }

}
