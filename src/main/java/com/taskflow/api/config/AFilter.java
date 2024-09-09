package com.taskflow.api.config;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

public class AFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {

        // request에 babo 속성을 추가
        if (request.getAttribute("babo") == null) {
            request.setAttribute("babo", 5);
        } else {
            Integer babo = (Integer) request.getAttribute("babo");
            request.setAttribute("babo", babo + 1);
        }

        // 다음 필터로 요청을 전달
        try {
            filterChain.doFilter(request, response);
        } catch (IOException | ServletException e) {
            e.printStackTrace();
        }
    }

}
