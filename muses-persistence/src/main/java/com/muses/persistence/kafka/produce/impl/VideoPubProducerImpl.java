package com.muses.persistence.kafka.produce.impl;

import com.muses.common.constant.VideoProgramConstant;
import com.muses.persistence.kafka.produce.IVideoPubProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * @ClassName VideoAuditProducer
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/11/28 14:57
 */
@Slf4j
@Service
public class VideoPubProducerImpl implements IVideoPubProducer {

    @Autowired
    @Qualifier("pubVideoKafkaTemplate")
    private KafkaTemplate<String, String> kafkaTemplate;


    public void sendMessage(String message) {
        //SpringBoot3的写法
        CompletableFuture<SendResult<String, String>> completableFuture = kafkaTemplate.send(VideoProgramConstant.VIDEO_PUB_TOPIC, message);

        //执行成功回调
        completableFuture.thenAccept(result -> log.debug("message send success :{}", message));
        //执行失败回调
        completableFuture.exceptionally(e -> {
            log.error("video pub message send failure {}", message, e);
            return null;
        });
    }

    public void sendMessage(String key, String message) {
        log.info("notify video pub message by key, key is {} -> message is {}", key, message);
        //SpringBoot3的写法
        CompletableFuture<SendResult<String, String>> completableFuture = kafkaTemplate.send(VideoProgramConstant.VIDEO_PUB_TOPIC, key, message);

        //执行成功回调
        completableFuture.thenAccept(result -> log.debug("message send success :{}", message));
        //执行失败回调
        completableFuture.exceptionally(e -> {
            log.error("video pub message send failure {}", message, e);
            return null;
        });
    }
}
