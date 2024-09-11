package com.taskflow.api.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AccessTokenProvider extends JwtProviderTemplate {

    public AccessTokenProvider(
            @Value("${jwt.access-token.secret-key}") String secretKey,
            @Value("${jwt.access-token.expiration}") Integer jwtExpiration) {
        super.secretKey = secretKey;
        super.jwtExpiration = jwtExpiration;
    }
}
