package com.muses.persistence.mysql.service.impl;

import com.muses.persistence.mysql.entity.LiveRoom;
import com.muses.persistence.mysql.repo.ILiveRoomRepo;
import com.muses.persistence.mysql.service.ILiveRoomRepoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @ClassName LiveRoomRepoService
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/10 13:39
 */
@Slf4j
@Component
@Transactional(value = "transactionManagerMysql")
public class LiveRoomRepoService implements ILiveRoomRepoService {

    @Autowired
    private ILiveRoomRepo liveRoomRepo;

    @Override
    public LiveRoom save(LiveRoom liveRoom) {
        return liveRoomRepo.save(liveRoom);
    }
}
