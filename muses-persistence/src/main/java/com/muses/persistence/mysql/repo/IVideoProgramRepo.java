package com.muses.persistence.mysql.repo;

import com.muses.persistence.mysql.entity.VideoProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @ClassName VideoProgramRepo
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/7 10:33
 */
public interface IVideoProgramRepo extends JpaRepository<VideoProgram, String>, JpaSpecificationExecutor<VideoProgram> {

    List<VideoProgram> findByUserId(String userId);

}
