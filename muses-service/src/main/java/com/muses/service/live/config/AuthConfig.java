package com.muses.service.live.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName AuthConfig
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/12 11:01
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.auth")
public class AuthConfig {

    private String jwtSecretKey;

    private String algorithm;

    private long jwtExpireTime;

}
