package com.taskflow.api.security.jwt;

import java.io.IOException;
import java.util.Optional;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.taskflow.api.entity.UserEntity;
import com.taskflow.api.service.UserService;
import com.taskflow.api.utils.CookieUtils;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtProviderFilter extends OncePerRequestFilter {

    private final AccessTokenProvider accessTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("jwt필터");

        // 로그인 요청은 건너뜀
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/api/v1/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = parseBearerToken(request);

            // 1. Access Token이 없을 때
            if (token == null) {
                // Refresh Token을 가져옴
                Optional<Cookie> refreshTokenCookie = CookieUtils.getCookie(request, "refresh-token");

                // 1-1. refresh Token이 있으면
                if (refreshTokenCookie.isPresent()) {
                    String refreshToken = refreshTokenCookie.get().getValue();

                    // Refresh Token을 검증하고, 유효하면 새로운 Access Token 발급
                    JwtSubjectVO jwtSubjectVO = refreshTokenProvider.validate(refreshToken);

                    // Refresh Token이 검증된 경우
                    if (jwtSubjectVO != null) {
                        String jwt = accessTokenProvider.create(jwtSubjectVO);

                        // UserService에서 유저 정보 확인 및 Refresh Token 일치 확인
                        UserEntity user = userService.findByEmailAndIsOAuth(jwtSubjectVO.getEmail(),
                                jwtSubjectVO.isOAuth());

                        if (refreshToken.equals(user.getRefreshToken())) {
                            // 새로운 Access Token 생성
                            String newAccessToken = accessTokenProvider.create(jwtSubjectVO);

                            // 새로운 Access Token을 응답 헤더에 추가
                            response.setHeader("Authorization", "Bearer " + newAccessToken);

                            // SecurityContext에 인증 정보 설정
                            setAuthentication(jwtSubjectVO.getEmail());
                        } else {
                            // Refresh Token이 일치하지 않거나 유저를 찾을 수 없으면 인증 실패 처리
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            return;
                        }
                    } else {
                        // Refresh Token이 유효하지 않으면 인증 실패 처리
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                    }
                }

                // 1-2. Refresh Token이 없으면, 다음 체인으로 이동
                filterChain.doFilter(request, response);
                return;
            }

            // 2. Access Token이 존재하는 경우 SecurityContext에 인증 정보 설정

            JwtSubjectVO accessToken = accessTokenProvider.validate(token);
            // 2-1. Access Token이 유효한 경우
            if (accessToken != null) {
                String email = accessToken.getEmail();
                setAuthentication(email);
            } else {
                // Access Token이 유효하지 않으면 인증 실패 처리
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

        } catch (Exception e) {
            // 예외 처리 로직
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        // 요청이 정상적으로 처리되었으므로 다음 필터 체인 실행
        filterChain.doFilter(request, response);
    }

    private String parseBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        boolean hasAuthorization = StringUtils.hasText(authorization);
        if (!hasAuthorization)
            return null;

        boolean isBearer = authorization.startsWith("Bearer ");
        if (!isBearer)
            return null;

        String token = authorization.substring(7);
        return token;
    }

    private void setAuthentication(String email) {
        // 인증 정보 생성
        AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                email, null, AuthorityUtils.NO_AUTHORITIES);

        // SecurityContext 설정
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

}