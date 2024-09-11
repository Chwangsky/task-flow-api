package com.taskflow.api.security.jwt;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class JwtProviderTemplate {

    protected String secretKey;

    protected Integer jwtExpiration;

    // JWT 생성
    // isOAuth: 초기 로그인 시 OAuth 로그인 여부
    public final String create(JwtSubjectVO jwtSubjectVO) {
        Date expiredDate = Date.from(Instant.now().plus(jwtExpiration, ChronoUnit.SECONDS));
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        String jwt = Jwts.builder()
                .signWith(key)
                .subject(jwtSubjectVO.toString())
                .issuedAt(Date.from(Instant.now()))
                .expiration(expiredDate)
                .compact();

        log.info("JWT CREATED!");

        return jwt;
    }

    /**
     * 
     * @param jwt string
     * @return 만약 validate에 실패한 경우, null값을 리턴한다.
     *         만약 validate에 성공한 경우, JwtSubjectVO 값을 리턴한다.
     */
    public final JwtSubjectVO validate(String jwt) {
        Claims claims = null;
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(jwt);
            claims = claimsJws.getPayload();

        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", jwt);
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", jwt);
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", jwt);
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", jwt);
        }

        String subject = claims.getSubject();
        log.info("VALIDATED!");

        return JwtSubjectVO.fromString(subject);
    }

}
