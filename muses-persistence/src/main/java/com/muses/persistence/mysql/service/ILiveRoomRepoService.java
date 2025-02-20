package com.muses.persistence.mysql.service;

import com.muses.persistence.mysql.entity.LiveRoom;

/**
 * @ClassName ILiveRoomRepoService
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/10 13:39
 */
public interface ILiveRoomRepoService {
    LiveRoom save(LiveRoom liveRoom);
}
