package com.muses.service.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName FileStoreConfig
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/12/3 13:17
 */
@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "app.file-store")
public class FileStoreConfig {
    private int previewExpireSeconds;
}
