package com.muses.persistence.mysql.service.impl;

import com.muses.persistence.mysql.entity.VideoProgramInteract;
import com.muses.persistence.mysql.repo.IVideoProgramInteractRepo;
import com.muses.persistence.mysql.service.IVideoInteractRepoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ClassName VideoInteractRepoService
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/11/4 16:33
 */
@Slf4j
@Component
@Transactional(value = "transactionManagerMysql")
public class VideoInteractRepoService implements IVideoInteractRepoService {

    @Autowired
    private IVideoProgramInteractRepo videoProgramInteractService;

    @Override
    public VideoProgramInteract findById(String id) {
        return videoProgramInteractService.findById(id).orElse(null);
    }

    @Override
    public List<VideoProgramInteract> findByIdList(List<String> ids) {
        return videoProgramInteractService.findAllById(ids);
    }

    @Override
    public VideoProgramInteract save(VideoProgramInteract videoProgramInteract) {
        return videoProgramInteractService.save(videoProgramInteract);
    }


    @Override
    public void updateLikesByIdAndVersion(int like, int newVersion, String id, int oldVersion) {
        videoProgramInteractService.updateLikesByIdAndVersion(like, newVersion, id, oldVersion);
    }

    @Override
    public void updatePlayByIdAndVersion(int like, int newVersion, String id, int oldVersion){
        videoProgramInteractService.updateLikesByIdAndVersion(like, newVersion, id, oldVersion);
    }

}
