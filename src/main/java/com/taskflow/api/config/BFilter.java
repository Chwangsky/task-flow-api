package com.taskflow.api.config;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

public class BFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {

        // babo 속성이 있으면 그 값을 출력
        Object babo = request.getAttribute("babo");
        if (babo != null) {
            System.out.println("BFilter: babo = " + babo);
        }
        // 다음 필터로 요청을 전달
        try {
            Cookie cookie = new Cookie("MyAccessToken2", "123");
            cookie.setHttpOnly(true);
            cookie.setValue("ohmygodmyvaluechanged!");
            response.addCookie(cookie);

            filterChain.doFilter(request, response);
        } catch (IOException | ServletException e) {
            e.printStackTrace();
        }
    }

}