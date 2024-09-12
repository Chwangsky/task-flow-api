package com.taskflow.api.auth.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.taskflow.api.auth.dto.request.EmailVerifyRequestDTO;
import com.taskflow.api.entity.UserEntity;
import com.taskflow.api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final UserRepository userRepository;

    public ResponseEntity<?> verifyToken(EmailVerifyRequestDTO dto) {

        Optional<UserEntity> user;

        try {
            user = userRepository.findById(dto.getUserId());
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }

        if (user.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        UserEntity userEntity = user.get();

        // 토큰 만료 여부 확인
        if (userEntity.getEmailVerifiedTokenExpireAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.GONE).body("");
        }

        // 토큰 일치 여부 확인
        if (!userEntity.getEmailVerifiedToken().equals(dto.getToken())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }

        return ResponseEntity.status(HttpStatus.OK).body("");

    }

}
