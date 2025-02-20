package com.muses.persistence.mysql.repo;

import com.muses.persistence.mysql.entity.LiveProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @ClassName LiveProgramRepo
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/10 11:37
 */
public interface ILiveProgramRepo extends JpaRepository<LiveProgram, String>, JpaSpecificationExecutor<LiveProgram> {

    List<LiveProgram> findByAnchorAndStateIn(String userId, List<Integer> stateList);

    LiveProgram findByLiveRoomId(String liveRoomId);



}
