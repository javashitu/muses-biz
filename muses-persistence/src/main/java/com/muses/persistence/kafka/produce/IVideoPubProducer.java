package com.muses.persistence.kafka.produce;

/**
 * @ClassName IVideoPubProducer
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/12/4 13:51
 */
public interface IVideoPubProducer {
    void sendMessage(String message);

    void sendMessage(String key, String message);
}
