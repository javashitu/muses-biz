package com.muses.persistence.mysql.service;

import com.muses.persistence.mysql.entity.VideoProgramInteract;

import java.util.List;

/**
 * @ClassName IVideoInteractRepoService
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/11/4 16:32
 * 缩写一下，按道理应该拼写成IVideoProgramInteractRepoService，但是这个名字太长了
 */
public interface IVideoInteractRepoService {

    VideoProgramInteract findById(String id);

    List<VideoProgramInteract> findByIdList(List<String> ids);

    VideoProgramInteract save(VideoProgramInteract videoProgramInteract);

    void updatePlayByIdAndVersion(int like, int newVersion, String id, int oldVersion);

    void updateLikesByIdAndVersion(int like, int newVersion, String id, int oldVersion);

}
