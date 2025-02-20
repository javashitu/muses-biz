package com.muses.persistence.kafka.consume;

import com.muses.common.constant.VideoProgramConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * @ClassName VideoPubConsumer
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/11/28 15:25
 */
@Slf4j
@Component
public class VideoPubConsumer {

    @KafkaListener(topics = VideoProgramConstant.VIDEO_PUB_TOPIC, groupId="muses_biz_group", containerFactory = "pubVideoKafkaListenerContainerFactory")
    public void listen(String record, Acknowledgment acknowledgment) {
        // 在这里处理你的消息
        log.info("Received video pub message in topic " + record + ": " + record);

        // 确认消息
        acknowledgment.acknowledge();
    }

}
