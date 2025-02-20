package com.muses.persistence.mysql.repo;

import com.muses.persistence.mysql.entity.VideoProgramInteract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * @ClassName IVideoProgramInteractRepo
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/11/4 16:30
 */
public interface IVideoProgramInteractRepo extends JpaRepository<VideoProgramInteract, String> {

    /**
     * jpa要求所有更新必须有事务
     */
    @Modifying
    @Transactional
    @Query(value = "update video_program_interact set play = ?1 , version = ?2 where id = ?3 and version = ?4", nativeQuery = true)
    void updatePlayByIdAndVersion(int play, int newVersion, String id, int oldVersion);

    @Modifying
    @Transactional
    @Query(value = "update video_program_interact set likes = ?1 , version = ?2 where id = ?3 and version = ?4", nativeQuery = true)
    void updateLikesByIdAndVersion(int like, int newVersion, String id, int oldVersion);
}
