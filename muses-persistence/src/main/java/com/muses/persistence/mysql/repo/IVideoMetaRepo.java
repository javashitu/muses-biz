package com.muses.persistence.mysql.repo;

import com.muses.persistence.mysql.entity.VideoMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @ClassName IVideoMetaRepo
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/12/19 10:08
 */
public interface IVideoMetaRepo extends JpaRepository<VideoMeta, String>, JpaSpecificationExecutor<VideoMeta> {
    List<VideoMeta> findByProgramId(String programId);
}
