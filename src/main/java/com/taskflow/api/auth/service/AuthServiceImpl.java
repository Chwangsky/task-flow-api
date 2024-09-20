package com.taskflow.api.auth.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taskflow.api.auth.dto.request.DuplicatedEmailRequestDTO;
import com.taskflow.api.auth.dto.request.DuplicatedNicknameRequestDTO;
import com.taskflow.api.auth.dto.request.LocalSignUpRequestDTO;
import com.taskflow.api.auth.dto.response.DuplicatedEmailResponseDTO;
import com.taskflow.api.auth.dto.response.DuplicatedNicknameResponseDTO;
import com.taskflow.api.auth.dto.response.LocalSignUpResponseDTO;
import com.taskflow.api.auth.dto.response.ReSendEmailResponseDTO;
import com.taskflow.api.auth.email.EmailProvider;
import com.taskflow.api.common.dto.ResponseDTO;
import com.taskflow.api.common.utils.EmailVerifyTokenGenerator;
import com.taskflow.api.entity.UserEntity;
import com.taskflow.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Value("${email.verifytoken.expiration}")
    private String EMAIL_VERIFY_TOKEN_EXPIRATION;

    private final PasswordEncoder passwordEncoder;

    private final EmailProvider emailProvider;

    private final UserRepository userRepository;

    @Override
    public UserEntity findByEmailAndIsOAuth(String email, boolean isOAuth) {
        return userRepository.findByEmailAndIsOAuth(email, isOAuth)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found with email: " + email + " and isOAuth: " + isOAuth));
    }

    @Transactional
    @Override
    public ResponseEntity<? super LocalSignUpResponseDTO> localSignUp(LocalSignUpRequestDTO signUpRequestDTO) {

        // 1. 이메일 및 닉네임 중복 여부 확인

        try {
            // email + oauth인증 여부를가 모두 같아야만 중복된 이메일로 취급한다.
            if (checkIfEmailDuplicated(signUpRequestDTO.getEmail()))
                return LocalSignUpResponseDTO.duplicatedEmail();
            if (checkIfNicknameDuplicated(signUpRequestDTO.getNickname()))
                return LocalSignUpResponseDTO.duplicatedNickname();
        } catch (Exception e) {
            return ResponseDTO.databaseError();
        }

        String token = EmailVerifyTokenGenerator.generateVerifyToken();

        UserEntity userEntity = UserEntity.emailVerifiyEntityFromDTO(
                signUpRequestDTO,
                passwordEncoder.encode(signUpRequestDTO.getPassword()),
                token,
                LocalDateTime.now().plusSeconds(Long.valueOf(EMAIL_VERIFY_TOKEN_EXPIRATION)));

        try {
            userEntity = userRepository.save(userEntity);
        } catch (Exception e) {
            return ResponseDTO.databaseError();
        }
        // @Async 이메일 전송
        emailProvider.sendCertificationMail(signUpRequestDTO.getEmail(), token, userEntity.getId());

        return LocalSignUpResponseDTO.success(userEntity);
    }

    @Transactional
    public ResponseEntity<? super ReSendEmailResponseDTO> reSendEmail(Long userId) {

        Optional<UserEntity> user;
        try {
            user = userRepository.findById(userId);
        } catch (Exception e) {
            return ResponseDTO.databaseError();
        }

        // userId가 유효하지 않은 경우
        if (user.isEmpty()) {
            return ReSendEmailResponseDTO.noSuchUser();
        }

        UserEntity userEntity = user.get();

        // 이메일 중복 여부
        if (userEntity.isEmailVerified() == true) {
            return ReSendEmailResponseDTO.alreadyVerifiedEmail();
        }

        //
        String token = EmailVerifyTokenGenerator.generateVerifyToken();

        userEntity.setEmailVerifiedToken(token);
        userEntity.setEmailVerifiedTokenExpireAt(
                LocalDateTime.now().plusSeconds(Long.valueOf(EMAIL_VERIFY_TOKEN_EXPIRATION)));

        try {
            userEntity = userRepository.save(userEntity);
        } catch (Exception e) {
            return ResponseDTO.databaseError();
        }
        // @Async 이메일 전송
        emailProvider.sendCertificationMail(userEntity.getEmail(), token, userEntity.getId());

        return ReSendEmailResponseDTO.success();

    }

    @Transactional(readOnly = true)
    private Boolean checkIfEmailDuplicated(String email) {
        return userRepository.existsByEmailAndIsOAuth(email, false);
    }

    @Transactional(readOnly = true)
    private Boolean checkIfNicknameDuplicated(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    @Override
    public ResponseEntity<? super DuplicatedEmailResponseDTO> checkEmailDuplicated(DuplicatedEmailRequestDTO dto) {
        try {
            if (checkIfEmailDuplicated(dto.getEmail()))
                return DuplicatedEmailResponseDTO.duplicated(true);
        } catch (Exception e) {
            return ResponseDTO.databaseError();
        }
        return DuplicatedEmailResponseDTO.duplicated(false);
    }

    @Override
    public ResponseEntity<? super DuplicatedNicknameResponseDTO> checkNicknameDuplicated(
            DuplicatedNicknameRequestDTO dto) {
        try {
            if (checkIfNicknameDuplicated(dto.getNickname()))
                return DuplicatedNicknameResponseDTO.duplicated(true);
        } catch (Exception e) {
            return ResponseDTO.databaseError();
        }
        return DuplicatedNicknameResponseDTO.duplicated(false);
    }

}
