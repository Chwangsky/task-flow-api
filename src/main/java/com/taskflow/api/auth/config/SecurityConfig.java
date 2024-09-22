package com.taskflow.api.auth.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.taskflow.api.auth.filter.jwt.AccessTokenProvider;
import com.taskflow.api.auth.filter.jwt.JwtProviderFilter;
import com.taskflow.api.auth.filter.jwt.RefreshTokenProvider;
import com.taskflow.api.auth.filter.login.CustomAuthenticationFailureHandler;
import com.taskflow.api.auth.filter.login.CustomAuthenticationProvider;
import com.taskflow.api.auth.filter.login.CustomAuthenticationSuccessHandler;
import com.taskflow.api.auth.filter.login.CustomUsernamePasswordAuthenticationFilter;
import com.taskflow.api.auth.handler.OAuth2LoginSuccessHandler;
import com.taskflow.api.auth.service.AuthService;
import com.taskflow.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity(debug = true) // TODO: debug 모드 추후 수정
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    private final UserRepository userRepository;

    private final AccessTokenProvider accessTokenProvider;

    private final RefreshTokenProvider refreshTokenProvider;

    private final AuthService authService;

    private final PasswordEncoder passwordEncoder;

    private final DefaultOAuth2UserService oAuth2UserService;

    private final LogoutSuccessHandler logoutSuccessHandler;

    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(userRepository, passwordEncoder);
    }

    @Bean
    public JwtProviderFilter jwtProviderFilter() {

        List<AntPathRequestMatcher> excludedPaths = new ArrayList<>();
        excludedPaths.add(new AntPathRequestMatcher("/api/v1/auth/**")); // jwt 필터 제외 설정
        excludedPaths.add(new AntPathRequestMatcher("/email-verify", "POST"));
        return new JwtProviderFilter(accessTokenProvider, refreshTokenProvider, authService,
                excludedPaths);
    }

    @Bean
    public CustomUsernamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter(
            AuthenticationManager authManager) {
        CustomUsernamePasswordAuthenticationFilter filter = new CustomUsernamePasswordAuthenticationFilter(
                AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/api/v1/auth/login")); // 로그인 경로
        filter.setAuthenticationManager(authManager);
        filter.setAuthenticationSuccessHandler(customAuthenticationSuccessHandler);
        filter.setAuthenticationFailureHandler(customAuthenticationFailureHandler);
        return filter;
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http
                .getSharedObject(AuthenticationManagerBuilder.class);
        // CustomAuthenticationProvider를 AuthenticationManager에 등록
        authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception {
        http
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource()))
                .csrf(CsrfConfigurer::disable) // restapi 서버이므로 csrf 비활성화
                .httpBasic(HttpBasicConfigurer::disable) // 기본 Http설정 비활성화
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // JWT 인증 방식을
                                                                                // 사용하므로
                                                                                // STATELESS
                )
                .authorizeHttpRequests(configureAuthorization())
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/api/v1/auth/logout")) // 로그아웃
                                                                                                // 경로
                                                                                                // 설정
                        .logoutSuccessHandler(logoutSuccessHandler)
                        .deleteCookies("JSESSIONID"))
                .oauth2Login(oauth2 -> oauth2
                        // OAuth2 제공자로부터 리다이렉션을 받을 기본 URI를 설정.
                        .redirectionEndpoint(
                                endpoint -> endpoint.baseUri("/oauth2/callback/**"))
                        // 사용자가 인증을 완료한 후 사용자에 대한 추가 정보를 Authorization Server로부터 얻을 수 있는 방법 설정
                        .userInfoEndpoint(endpoint -> endpoint.userService(oAuth2UserService))
                        .successHandler(oAuth2LoginSuccessHandler))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new FailedAuthenticationEntryPoint()))

                .addFilterBefore(jwtProviderFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(customUsernamePasswordAuthenticationFilter(authManager),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> configureAuthorization() {
        return request -> request
                .requestMatchers("/", "/api/v1/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/email-verify").permitAll()
                .anyRequest().authenticated();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}

@Slf4j
class FailedAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException)
            throws IOException, ServletException {

        log.error("ExceptionTranslationFilter Activated!", authException);
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write("{ \"code\": \"NP\", \"message\": \"Do not have permission.\" }");

    }

}