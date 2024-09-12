package com.taskflow.api.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskflow.api.auth.dto.request.DuplicatedEmailRequestDTO;
import com.taskflow.api.auth.dto.request.DuplicatedNicknameRequestDTO;
import com.taskflow.api.auth.dto.request.LocalSignUpRequestDTO;
import com.taskflow.api.auth.dto.response.DuplicatedEmailResponseDTO;
import com.taskflow.api.auth.dto.response.DuplicatedNicknameResponseDTO;
import com.taskflow.api.auth.dto.response.LocalSignUpResponseDTO;
import com.taskflow.api.auth.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<? super LocalSignUpResponseDTO> localSignUp(@RequestBody LocalSignUpRequestDTO dto) {
        return authService.localSignUp(dto);
    }

    @GetMapping("/duplicated-nickname-check")
    public ResponseEntity<? super DuplicatedNicknameResponseDTO> checkNicknameDuplicated(
            @RequestBody DuplicatedNicknameRequestDTO dto) {
        return authService.checkNicknameDuplicated(dto);
    }

    @GetMapping("/duplicated-email-check")
    public ResponseEntity<? super DuplicatedEmailResponseDTO> checkEmailDuplicated(
            @RequestBody DuplicatedEmailRequestDTO dto) {
        return authService.checkEmailDuplicated(dto);
    }

}
