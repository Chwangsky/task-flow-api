package com.taskflow.api.auth.filter.jwt;

import java.io.IOException;
import java.util.List;
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.taskflow.api.auth.service.AuthService;
import com.taskflow.api.global.entity.UserEntity;
import com.taskflow.api.global.utils.CookieUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtProviderFilter extends OncePerRequestFilter {

    private final AccessTokenProvider accessTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;
    private final AuthService authService;
    private final List<AntPathRequestMatcher> excludedMatchers;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return excludedMatchers.stream()
                .anyMatch(matcher -> matcher.matches(request));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = parseBearerToken(request);

            // 1. Access Token이 없을 때
            if (token == null) {
                // Refresh Token을 가져옴
                Optional<Cookie> refreshTokenCookie = CookieUtils.getCookie(request, "refreshToken");

                // 1-1. refresh Token이 있으면
                if (refreshTokenCookie.isPresent()) {
                    String refreshToken = refreshTokenCookie.get().getValue();

                    // Refresh Token을 검증하고, 유효하면 새로운 Access Token 발급
                    JwtSubjectVO jwtSubjectVO = refreshTokenProvider.validate(refreshToken);

                    // Refresh Token이 검증된 경우
                    if (jwtSubjectVO != null) {

                        // UserService에서 유저 정보 확인 및 Refresh Token 일치 확인
                        UserEntity userEntity = null;
                        try {
                            userEntity = authService.findByEmailAndIsOAuth(jwtSubjectVO.getEmail(),
                                    jwtSubjectVO.isOAuth());
                        } catch (Exception e) {
                            log.warn("refresh 토큰 확인 중 데이터베이스 오류입니다.");
                        }

                        if (userEntity != null && refreshToken.equals(userEntity.getRefreshToken())) {
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
                // 1-2. Access Token, Refresh Token 둘 다 없으면 다음 필터로
                else {
                    filterChain.doFilter(request, response);
                    return;
                }

            }

            // 2. Access Token이 있을 때 SecurityContext에 인증 정보 설정

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
            filterChain.doFilter(request, response);
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