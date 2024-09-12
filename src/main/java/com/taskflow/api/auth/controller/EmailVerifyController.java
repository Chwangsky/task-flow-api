package com.taskflow.api.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskflow.api.auth.dto.request.EmailVerifyRequestDTO;
import com.taskflow.api.auth.service.EmailService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/verify")
public class EmailVerifyController {

    private final EmailService emailService;

    @PostMapping()
    public ResponseEntity<?> postMethodName(@RequestBody EmailVerifyRequestDTO emailVerifyRequestDTO) {

        return emailService.verifyToken(emailVerifyRequestDTO);
    }

}
