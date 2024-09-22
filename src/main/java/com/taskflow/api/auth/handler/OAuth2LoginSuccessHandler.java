package com.taskflow.api.auth.handler;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.taskflow.api.auth.filter.jwt.AccessTokenProvider;
import com.taskflow.api.auth.filter.jwt.JwtSubjectVO;
import com.taskflow.api.auth.filter.jwt.RefreshTokenProvider;
import com.taskflow.api.common.ResponseCode;
import com.taskflow.api.common.ResponseMessage;
import com.taskflow.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final AccessTokenProvider accessTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        // OAuth2 로그인 성공 후 사용자 정보 가져오기
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getName(); // 사용자 이메일

        // Access Token 생성
        String accessToken = accessTokenProvider.create(
                JwtSubjectVO.builder()
                        .email(email)
                        .isOAuth(true)
                        .build());

        // Refresh Token 생성
        String refreshToken = refreshTokenProvider.create(
                JwtSubjectVO.builder()
                        .email(email)
                        .isOAuth(true)
                        .build());

        // Access Token 및 Refresh Token 만료 시간 설정
        int accessTokenExpirationTime = 3600; // Access Token 만료 시간 (초 단위)
        int refreshTokenExpirationTime = 604800; // Refresh Token 만료 시간 (1주일)

        String googleAccessToken = oAuth2User.getAttribute("access_token");
        String googleRefreshToken = oAuth2User.getAttribute("refresh_token");
        Date googleAccessTokenExpiry = oAuth2User.getAttribute("expires_at");

        // 응답 데이터 구성
        String code = ResponseCode.SUCCESS;
        String message = ResponseMessage.SUCCESS;

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", code);
        responseData.put("message", message);
        responseData.put("accessToken", accessToken);
        responseData.put("refreshToken", refreshToken);

        // 응답을 JSON 형식으로 변환 및 전송
        String jsonResponse = objectMapper.writeValueAsString(responseData);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(jsonResponse);

        // 프론트엔드로 리디렉션
        response.sendRedirect("http://localhost:3000/oauth-response/" + accessToken + "/" + accessTokenExpirationTime);
    }
}
