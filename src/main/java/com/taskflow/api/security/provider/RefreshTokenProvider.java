package com.taskflow.api.security.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RefreshTokenProvider extends JwtProviderTemplate {

    @Value("${jwt.refresh-token.secret-key}")
    private String secretKey;

    @Value("${jwt.refresh-token.expiration-ms}")
    private Integer jwtExpirationMs;

}
