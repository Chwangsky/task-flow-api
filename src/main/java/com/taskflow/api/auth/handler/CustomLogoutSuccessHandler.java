package com.taskflow.api.auth.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskflow.api.global.ResponseCode;
import com.taskflow.api.global.ResponseMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Autowired
    private ObjectMapper objectMapper; // Jackson 라이브러리를 사용하여 JSON 변환

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        // 로그아웃 성공 시 수행할 로직
        if (authentication != null) {
            log.info("로그아웃 핸들러 동작");
        }

        // JSON 응답 생성
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("code", ResponseCode.SUCCESS);
        responseMap.put("message", ResponseMessage.SUCCESS);

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(objectMapper.writeValueAsString(responseMap));
    }
}
