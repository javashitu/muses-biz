package com.muses.persistence.mysql.service;

import com.muses.persistence.mysql.entity.VideoProgram;

import java.util.List;

/**
 * @ClassName IVideoProgramRepoService
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/9 10:54
 */
public interface IVideoProgramRepoService {

    VideoProgram findById(String id);

    List<VideoProgram> findByIdList(List<String> ids);


    VideoProgram save(VideoProgram videoProgram);


    List<VideoProgram> findByUserId(String userId, int pageNum);

    List<VideoProgram> findAll(int pageNum);

}
