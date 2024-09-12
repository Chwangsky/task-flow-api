package com.taskflow.api.auth.service;

import org.springframework.http.ResponseEntity;

import com.taskflow.api.auth.dto.request.LocalSignUpRequestDTO;
import com.taskflow.api.auth.dto.response.LocalSignUpResponseDTO;
import com.taskflow.api.entity.UserEntity;

public interface AuthService {
    UserEntity findByEmailAndIsOAuth(String email, boolean isOAuth);

    ResponseEntity<? super LocalSignUpResponseDTO> localSignUp(LocalSignUpRequestDTO signUpRequestDTO);
}
