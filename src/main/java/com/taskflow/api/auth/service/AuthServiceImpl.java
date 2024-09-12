package com.taskflow.api.auth.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.taskflow.api.auth.dto.request.LocalSignUpRequestDTO;
import com.taskflow.api.auth.dto.response.LocalSignUpResponseDTO;
import com.taskflow.api.common.dto.ResponseDTO;
import com.taskflow.api.entity.UserEntity;
import com.taskflow.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    @Override
    public UserEntity findByEmailAndIsOAuth(String email, boolean isOAuth) {
        return userRepository.findByEmailAndIsOAuth(email, isOAuth)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found with email: " + email + " and isOAuth: " + isOAuth));
    }

    @Override
    public ResponseEntity<? super LocalSignUpResponseDTO> localSignUp(LocalSignUpRequestDTO signUpRequestDTO) {

        UserEntity user = UserEntity.builder()
                .email(signUpRequestDTO.getEmail())
                .password(passwordEncoder.encode(signUpRequestDTO.getPassword()))
                .isOAuth(false)
                .isEmailVerified(false)
                .build();

        try {
            user = userRepository.save(user);
            return LocalSignUpResponseDTO.success(user);
        } catch (Exception e) {
            return ResponseDTO.databaseError();
        }

    }
}
