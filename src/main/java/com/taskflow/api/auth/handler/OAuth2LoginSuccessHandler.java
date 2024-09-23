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
import com.taskflow.api.auth.filter.jwt.AccessTokenProvider;
import com.taskflow.api.auth.filter.jwt.JwtSubjectVO;
import com.taskflow.api.auth.filter.jwt.RefreshTokenProvider;
import com.taskflow.api.global.ResponseCode;
import com.taskflow.api.global.ResponseMessage;
import com.taskflow.api.global.entity.UserEntity;
import com.taskflow.api.global.entity.UserGoogleTokenEntity;
import com.taskflow.api.global.repository.UserGoogleTokenRepository;
import com.taskflow.api.global.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final UserGoogleTokenRepository userGoogleTokenRepository;
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

        // Google OAuth2 Access Token 및 기타 정보 가져오기
        String googleAccessToken = oAuth2User.getAttribute("access_token");
        String googleRefreshToken = oAuth2User.getAttribute("refresh_token");
        Date googleAccessTokenExpiry = oAuth2User.getAttribute("expires_at");

        // 사용자 정보 가져오기
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        // 사용자 Google OAuth2 토큰 정보를 저장/업데이트
        UserGoogleTokenEntity userGoogleToken = userGoogleTokenRepository.findById(user.getId()).orElseGet(() -> {
            return new UserGoogleTokenEntity(); // 기존에 저장된 토큰 정보가 없으면 새로 생성
        });

        userGoogleToken.setUser(user);
        userGoogleToken.setAccessToken(googleAccessToken);
        userGoogleToken.setRefreshToken(googleRefreshToken);
        userGoogleToken.setExpiresAt(
                googleAccessTokenExpiry.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
        userGoogleToken.setTokenType("Bearer"); // 토큰 타입 설정

        userGoogleTokenRepository.save(userGoogleToken); // 저장

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
        response.sendRedirect("http://localhost:3000/oauth-response/" + accessToken + "/" + 3600); // 3600: Access Token
                                                                                                   // 만료 시간 (1시간)
    }
}
