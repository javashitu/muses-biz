package com.muses.persistence.mysql.service;

import com.muses.persistence.mysql.entity.LiveProgram;

import java.util.List;

/**
 * @ClassName ILiveProgramRepoService
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/10 11:40
 */
public interface ILiveProgramRepoService {

    LiveProgram findById(String liveProgramId);

    public LiveProgram findByLiveRoomId(String liveRoomId);

    LiveProgram save(LiveProgram liveProgram);

    List<LiveProgram> findByUserIdAndState(String userId, List<Integer> stateList);

    List<LiveProgram> findByBeginTimeAndState(int pageNum,long beginTime, List<Integer> state);

    List<LiveProgram> findByBeginTimeAndStateAndAnchor(int pageNum,long beginTime, List<Integer> state, String userId);


}
