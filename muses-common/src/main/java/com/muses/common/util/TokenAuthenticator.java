package com.muses.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Date;
import java.util.Map;

/**
 * @ClassName TokenAuthenticator
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/12 13:57
 */
@Data
@Slf4j
public class TokenAuthenticator {

    private JwtAuthenticator jwtAuthenticator;

    private ObjectMapper mapper = new ObjectMapper();

    @Builder
    public TokenAuthenticator(long jwtExpireTime, String secretKey, String algorithm) {
        this.jwtAuthenticator = new JwtAuthenticator(jwtExpireTime, secretKey, algorithm, mapper);
    }

    public String createToken(Object o) {
        return jwtAuthenticator.createToken(o);
    }

    public Pair<Boolean, Claims> validToken(String token, String userId) {
        try {
            Claims claims = jwtAuthenticator.parseToken(token);
            if (claims == null) {
                log.error("valid token failure, can't parse token {}", token);
                return MutablePair.of(false, null);
            }
            String tokenUserId = mapper.readValue(claims.getSubject(), new TypeReference<Map<String, String>>() {
            }).get("userId");
            if (!StringUtils.equals(userId, tokenUserId)) {
                log.error("valid token failure, token not belong to user, userId {} -> token {}", userId, token);
                return MutablePair.of(false, null);
            }
            return MutablePair.of(true, claims);
        } catch (Exception e) {
            log.error("valid token error userId {} -> token {}", userId, token, e);
            return MutablePair.of(false, null);
        }
    }

    /**
     * todo 实际这里的过期校验应该还需要带有自动刷新token的能力
     */
    public boolean isExpire(Claims claims) {
        if (claims.getExpiration().after(new Date())) {
            log.info("token is valid");
            return false;
        }
        log.info("token is expire");
        return true;
    }
}
