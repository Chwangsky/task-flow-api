package com.taskflow.api.auth.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
import com.taskflow.api.auth.service.AuthService;
import com.taskflow.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

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

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(userRepository, passwordEncoder);
    }

    @Bean
    public JwtProviderFilter jwtProviderFilter() {
        return new JwtProviderFilter(accessTokenProvider, refreshTokenProvider, authService);
    }

    @Bean
    public CustomUsernamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter(
            AuthenticationManager authManager) {
        CustomUsernamePasswordAuthenticationFilter filter = new CustomUsernamePasswordAuthenticationFilter(
                new AntPathRequestMatcher("/api/v1/login", "POST"));
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
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // JWT 인증 방식을 사용하므로 STATELESS
                )
                .authorizeHttpRequests(configureAuthorization())
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new FailedAuthenticationEntryPoint()))

                .addFilterBefore(jwtProviderFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(customUsernamePasswordAuthenticationFilter(authManager),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> configureAuthorization() {
        return request -> request
                .requestMatchers("/verify/**").permitAll() // 이메일 인증 경로
                .requestMatchers("/", "/api/v1/auth/**", "/api/v1/search/**", "/file/**", "/oauth2/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/board/**", "/api/v1/user/*", "/api/v1/user").permitAll()
                .requestMatchers("/api/v1/auth/logout").permitAll()
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
        log.error("ExceptionTranslationFilter Activated!");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write("{ \"code\": \"NP\", \"message\": \"Do not have permission.\" }");

    }

}