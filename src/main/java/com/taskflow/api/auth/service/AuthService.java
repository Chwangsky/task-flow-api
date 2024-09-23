package com.taskflow.api.auth.service;

import org.springframework.http.ResponseEntity;

import com.taskflow.api.auth.dto.request.DuplicatedEmailRequestDTO;
import com.taskflow.api.auth.dto.request.DuplicatedNicknameRequestDTO;
import com.taskflow.api.auth.dto.request.LocalSignUpRequestDTO;
import com.taskflow.api.auth.dto.response.DuplicatedEmailResponseDTO;
import com.taskflow.api.auth.dto.response.DuplicatedNicknameResponseDTO;
import com.taskflow.api.auth.dto.response.LocalSignUpResponseDTO;
import com.taskflow.api.auth.dto.response.ReSendEmailResponseDTO;
import com.taskflow.api.global.entity.UserEntity;

public interface AuthService {
    UserEntity findByEmailAndIsOAuth(String email, boolean isOAuth);

    ResponseEntity<? super LocalSignUpResponseDTO> localSignUp(LocalSignUpRequestDTO signUpRequestDTO);

    ResponseEntity<? super DuplicatedEmailResponseDTO> checkEmailDuplicated(DuplicatedEmailRequestDTO dto);

    ResponseEntity<? super DuplicatedNicknameResponseDTO> checkNicknameDuplicated(
            DuplicatedNicknameRequestDTO dto);

    ResponseEntity<? super ReSendEmailResponseDTO> reSendEmail(Long userId);

}
