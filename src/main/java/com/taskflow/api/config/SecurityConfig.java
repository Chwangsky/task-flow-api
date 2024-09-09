package com.taskflow.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers("/test/**").permitAll() // "/public/**" 경로는 누구나 접근 가능
                                                .anyRequest().authenticated() // 그 외의 모든 요청은 인증 필요
                                )
                                .addFilterBefore(new AFilter(), UsernamePasswordAuthenticationFilter.class)
                                .addFilterBefore(new BFilter(), UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

}
