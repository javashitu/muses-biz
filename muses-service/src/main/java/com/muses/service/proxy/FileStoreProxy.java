package com.muses.service.proxy;

import com.google.common.collect.Maps;
import com.muses.persistence.mysql.entity.FileStore;
import com.muses.service.config.FileStoreConfig;
import com.muses.service.grpc.*;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

/**
 * @ClassName FileStoreProxy
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/10 19:49
 */
@Slf4j
@Component
public class FileStoreProxy {

    @GrpcClient("muses-engine")
    private FileStoreServiceGrpc.FileStoreServiceBlockingStub fileStoreStub;


    @GrpcClient("muses-engine")
    private MyHelloGrpc.MyHelloBlockingStub helloBlockingStub;

    @Autowired
    private FileStoreConfig fileStoreConfig;

    public Map<String, String> queryFilePreviewUrl(Map<String, FileStore> map) {
        log.info("begin query the file's visit url");
        if (MapUtils.isEmpty(map)) {
            return Collections.emptyMap();
        }

        QueryFileInfoReq.Builder requestBuilder = QueryFileInfoReq.newBuilder();

        Map<String, String> result = Maps.newHashMap();
        for (Map.Entry<String, FileStore> entry : map.entrySet()) {
            QueryFileInfo queryFileInfo = QueryFileInfo.newBuilder()
                    .setId(entry.getValue().getId())
                    .setPreviewExpireSeconds(fileStoreConfig.getPreviewExpireSeconds())
                    .build();
            requestBuilder.addQueryFileInfoList(queryFileInfo);
        }
        QueryFileInfoRsp response = fileStoreStub.queryVisitUrl(requestBuilder.build());
        if (CollectionUtils.isEmpty(response.getFileStoreInfoListList())) {
            return Collections.emptyMap();
        }
        response.getFileStoreInfoListList().forEach(fileStoreInfo -> result.put(fileStoreInfo.getId(), fileStoreInfo.getUrl()));
        return result;
    }
}
