package com.muses.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muses.common.enums.ServerErrorCodeEnums;
import com.muses.common.exception.ServerException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * @ClassName JwtAuthenticator
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/12 10:51
 */
@Slf4j
public class JwtAuthenticator {

    // 设置有效期30min
    private final long defaultExpireTime;

    // 生成密钥
    private final byte[] secretKey;

    // 密钥转 Base64 编码
    private final SignatureAlgorithm signatureAlgorithm;

    private final ObjectMapper mapper;

    @Builder
    public JwtAuthenticator(long defaultExpireTime, String secretKey, String algorithm, ObjectMapper objectMapper) {
        this.defaultExpireTime = defaultExpireTime;
        this.secretKey = secretKey.getBytes(StandardCharsets.UTF_8);
        this.signatureAlgorithm = SignatureAlgorithm.forName(algorithm);
        this.mapper = objectMapper;
    }

    public String createToken(String subject, long expireMillion) {
        // 当前时间
        long currentTime = DateTimeUtils.currentTime();
        // 过期时间
        long expireTime = currentTime + (expireMillion == 0 ? defaultExpireTime : expireMillion);

        // 构建jwt
        JwtBuilder builder = Jwts.builder()
                .setId(UUID.randomUUID() + "") //是JWT的唯一标识，根据业务需要，这个可以设置为一个不重复的值，主要用来作为令牌的唯一标识
                .setSubject(subject)
                .setIssuer("system")
                .setIssuedAt(new Date(currentTime))
                .signWith(signatureAlgorithm, secretKey)
                .setExpiration(new Date(expireTime));
        return builder.compact();
    }

    private String create(String subject) {
        return createToken(subject, 0L);
    }


    /**
     * 用jwt创建token
     *
     * @param data
     * @return
     */
    public String createToken(Object data) {
        try {
            return create(mapper.writeValueAsString(data));
        } catch (Exception e) {
            log.error("create jwt token failure, maybe need check data whether json ", e);
            throw ServerException.builder().serverErrorCodeEnums(ServerErrorCodeEnums.SERVER_ERROR).build();
        }
    }

    /**
     * 解析token
     *
     * @param token
     * @return
     */
    public Claims parseToken(String token) {
        Claims body = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return body;
    }

    /**
     * 解析token，返回json格式对象
     *
     * @param token
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> T parseToken(String token, Class<T> tClass) {
        try {
            Claims body = Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return mapper.readValue(body.getSubject(), tClass);
        } catch (Exception e) {
            log.error("parse jwt token failure, the token is {} ", token, e);
            throw ServerException.builder().serverErrorCodeEnums(ServerErrorCodeEnums.SERVER_ERROR).build();
        }
    }


    /**
     * 刷新Token
     *
     * @param token 旧的Token令牌
     * @return 新的Token令牌
     */
    public String refreshToken(String token) {
        try {
            // 解析旧的Token，获取负载信息
            Claims claims = parseToken(token);

            // 生成新的Token，设置过期时间和签名算法等参数
            return createToken(claims.getSubject());
        } catch (Exception e) {
            log.error("refresh jwt token failure, the token is {} ", token, e);
            throw ServerException.builder().serverErrorCodeEnums(ServerErrorCodeEnums.SERVER_ERROR).build();
        }
    }
}
