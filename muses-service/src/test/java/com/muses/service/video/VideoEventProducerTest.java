package com.muses.service.video;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.muses.common.util.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Map;

/**
 * @ClassName VideoEventPruducerTest
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/12/28 13:55
 */
@Slf4j
public class VideoEventProducerTest {

    @Test
    public void testProduceVideoEvent() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> eventMap = Maps.newHashMap();
        for (int i = 0; i < 3; i++) {
            eventMap.put("videoId", "76480" + i);
            eventMap.put("userId", "9527");
            eventMap.put("timestamp", String.valueOf(DateTimeUtils.currentTime()));
            eventMap.put("event", "view");
            System.out.println(mapper.writeValueAsString(eventMap));
            eventMap.put("event", "like");
            System.out.println(mapper.writeValueAsString(eventMap));
            if (i % 3 == 0) {
                eventMap.put("event", "click");
                System.out.println(mapper.writeValueAsString(eventMap));
                eventMap.put("event", "share");
                System.out.println(mapper.writeValueAsString(eventMap));
                eventMap.put("event", "coin");
                System.out.println(mapper.writeValueAsString(eventMap));
                eventMap.put("event", "collect");
                System.out.println(mapper.writeValueAsString(eventMap));
            }
            if (i % 3 == 1) {
                eventMap.put("userId", "9528");
                eventMap.put("event", "click");
                System.out.println(mapper.writeValueAsString(eventMap));
                eventMap.put("event", "share");
                System.out.println(mapper.writeValueAsString(eventMap));
            }
            log.info("produce video event {}", mapper.writeValueAsString(eventMap));
        }
    }
}
