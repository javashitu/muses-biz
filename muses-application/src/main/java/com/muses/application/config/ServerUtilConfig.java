package com.muses.application.config;

import com.muses.common.util.TokenAuthenticator;
import com.muses.common.util.IdGenerator;
import com.muses.common.util.JsonFormatter;
import com.muses.service.live.config.AuthConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName UtilConfig
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/7 10:01
 */
@Slf4j
@Configuration
public class ServerUtilConfig {

    @Bean
    public IdGenerator genIdGenerator() {
        return new IdGenerator(null);
    }

    @Bean
    public JsonFormatter genJsonFormatter() {
        return new JsonFormatter();
    }

    @Bean
    public TokenAuthenticator genAuthenticator(@Autowired AuthConfig authConfig) {
        return TokenAuthenticator.builder()
                .jwtExpireTime(authConfig.getJwtExpireTime())
                .secretKey(authConfig.getJwtSecretKey())
                .algorithm(authConfig.getAlgorithm()).build();
    }

}

