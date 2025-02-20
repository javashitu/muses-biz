package com.muses.command.kafka.consumer;

import com.muses.adapter.service.IVideoService;
import com.muses.common.constant.VideoProgramConstant;
import com.muses.common.util.JsonFormatter;
import com.muses.domain.kafka.message.VideoTranscodeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * @ClassName VideoTranscodeConsumer
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/12/11 9:55
 * 严格来说kafka入口应该是归属到cmd里的，但是这里如果移动到cmd去的话，会导致依赖变得复杂，而且没办法收敛在persistence里
 */
@Slf4j
@Component
public class VideoTranscodeConsumer {

    @Autowired
    private JsonFormatter jsonFormatter;

    @Autowired
    private IVideoService videoService;

    @KafkaListener(topics = VideoProgramConstant.VIDEO_TRANSCODE_TOPIC, groupId="muses_biz_group",containerFactory = "transcodeVideoKafkaListenerContainerFactory")
    public void listen(String record, Acknowledgment acknowledgment) {
        log.info("Received video transcode message in topic " + record + ": " + record);
        VideoTranscodeMessage videoTranscodeMessage = jsonFormatter.json2Object(record, VideoTranscodeMessage.class);
        videoService.updateTranscodeState(videoTranscodeMessage);
        // 确认消息
        acknowledgment.acknowledge();
    }
}
