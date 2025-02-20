package com.muses.com.util;

import com.muses.common.util.JwtAuthenticator;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName JwtAuthenticatorTest
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/12 17:19
 */
@Slf4j
public class JwtAuthenticatorTest {

    public static void main(String[] args) {
        JwtAuthenticator jwtAuthenticator = JwtAuthenticator.builder()
                .secretKey("8C6976E5B5410415BDE908BD4DEE15DFB167A9C873FC4BB8A81F6F2AB448A918")
                .defaultExpireTime(3600000L)
                .algorithm("HS256")
                .build();
        Map<String, String> map = new HashMap<>();
        String token = jwtAuthenticator.createToken(map);
        log.info("token is {}", token);
        Claims claims = jwtAuthenticator.parseToken(token);
    }
}
