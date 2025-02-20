package com.muses.service.video.file.stroe;

import com.google.common.collect.Maps;
import com.muses.persistence.mysql.entity.FileStore;
import com.muses.persistence.mysql.repo.IFileStoreRepo;
import com.muses.service.config.FileStoreConfig;
import com.muses.service.proxy.FileStoreProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * @ClassName FileStoreService
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/10 19:37
 */
@Slf4j
@Component
public class FileStoreService {

    @Autowired
    private IFileStoreRepo IFileStoreRepo;

    @Autowired
    private FileStoreProxy fileStoreProxy;

    @Autowired
    private FileStoreConfig fileStoreConfig;


    /**
     * videoFlag参数没用，只作为mock数据时的标识而已
     */
    public Map<String, String> getBatchFileVisitUrl(Set<String> fileStoreIdSet, boolean videoFlag) {
        Iterable<FileStore> fileStoreIterable = IFileStoreRepo.findAllById(fileStoreIdSet);
        Map<String, FileStore> fileStoreMap = Maps.newHashMap();
        fileStoreIterable.forEach(fileStore -> fileStoreMap.put(fileStore.getId(), fileStore));
        return fileStoreProxy.queryFilePreviewUrl(fileStoreMap);
    }
}
