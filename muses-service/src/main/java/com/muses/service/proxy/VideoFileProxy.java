package com.muses.service.proxy;

import com.muses.service.grpc.*;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

/**
 * @ClassName VideoFileProxy
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/12/2 18:59
 */
@Slf4j
@Component
public class VideoFileProxy {

    @GrpcClient("muses-recommend")
    private RecommendServiceGrpc.RecommendServiceBlockingStub recommendServiceGrpc;

    public void recommendVideo(){
        RecommendVideoReq recommendVideoReq = RecommendVideoReq.newBuilder().setUserId(9527L).build();
        RecommendVideoRsp rsp = recommendServiceGrpc.recommendVideo4User(recommendVideoReq);
        log.info("receive the grpc result {}", rsp.getVideoIdListList());
    }


}
