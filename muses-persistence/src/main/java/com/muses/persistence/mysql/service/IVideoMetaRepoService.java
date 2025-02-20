package com.muses.persistence.mysql.service;

import com.muses.persistence.mysql.entity.VideoMeta;

import java.util.List;

/**
 * @ClassName IVideoMetaRepoService
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/12/19 10:07
 */
public interface IVideoMetaRepoService {

    List<VideoMeta> findByVideoProgramId(String programId);

}
