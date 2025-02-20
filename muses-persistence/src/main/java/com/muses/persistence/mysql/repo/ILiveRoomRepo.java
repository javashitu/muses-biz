package com.muses.persistence.mysql.repo;

import com.muses.persistence.mysql.entity.LiveRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

/**
 * @ClassName ILiveRoomRepo
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/10 13:38
 */
@Component
public interface ILiveRoomRepo extends JpaRepository<LiveRoom, String> {
}
