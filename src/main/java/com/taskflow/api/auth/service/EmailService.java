package com.taskflow.api.auth.service;

import org.springframework.http.ResponseEntity;

import com.taskflow.api.auth.dto.request.EmailVerifyRequestDTO;

public interface EmailService {
    String verifyToken(EmailVerifyRequestDTO dto);

}
