package com.muses.persistence.mysql.service.impl;

import com.muses.persistence.mysql.entity.VideoMeta;
import com.muses.persistence.mysql.repo.IVideoMetaRepo;
import com.muses.persistence.mysql.service.IVideoMetaRepoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ClassName VideoMetaRepoService
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/12/19 10:07
 */
@Slf4j
@Component
@Transactional(value = "transactionManagerMysql")
public class VideoMetaRepoService implements IVideoMetaRepoService {

    @Autowired
    private IVideoMetaRepo videoMetaRepo;

    @Override
    public List<VideoMeta> findByVideoProgramId(String programId) {
        return videoMetaRepo.findByProgramId(programId);
    }
}
