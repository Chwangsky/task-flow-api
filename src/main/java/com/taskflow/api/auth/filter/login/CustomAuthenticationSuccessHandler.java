package com.taskflow.api.auth.filter.login;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.taskflow.api.auth.filter.jwt.AccessTokenProvider;
import com.taskflow.api.auth.filter.jwt.JwtSubjectVO;
import com.taskflow.api.auth.filter.jwt.RefreshTokenProvider;
import com.taskflow.api.global.utils.CookieUtils;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${jwt.refresh-token.expiration}")
    private Integer REFRESH_TOKEN_EXPIRATION;

    private final AccessTokenProvider accessTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        // 인증된 사용자의 이메일을 가져옴
        String email = authentication.getName();
        JwtSubjectVO jwt = JwtSubjectVO.builder().email(email).isOAuth(false).build();

        // 토큰 생성
        String accessToken = accessTokenProvider.create(jwt);
        String refreshToken = refreshTokenProvider.create(jwt);

        // refreshToken을 HttpOnly Cookie에 추가
        CookieUtils.addHttpOnlyCookie(response, "refreshToken", refreshToken, REFRESH_TOKEN_EXPIRATION, false);

        response.setHeader("Authorization", "Bearer " + accessToken); // accessToken을 Authorization 헤더에 추가
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        response.getWriter().write("{ \"code\": \"SU\", \"message\": \"Success.\" }");
    }

}
