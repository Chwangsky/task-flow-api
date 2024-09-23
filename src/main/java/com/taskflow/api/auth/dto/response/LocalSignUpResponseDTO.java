package com.taskflow.api.auth.dto.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.taskflow.api.global.ResponseCode;
import com.taskflow.api.global.ResponseMessage;
import com.taskflow.api.global.dto.ResponseDTO;
import com.taskflow.api.global.entity.UserEntity;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class LocalSignUpResponseDTO extends ResponseDTO {

    Long userId;
    String email;

    public static ResponseEntity<LocalSignUpResponseDTO> success(UserEntity user) {
        LocalSignUpResponseDTO responseDTO = LocalSignUpResponseDTO.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .code(ResponseCode.SUCCESS)
                .message(ResponseMessage.SUCCESS)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    public static ResponseEntity<ResponseDTO> duplicatedEmail() {
        ResponseDTO responseDTO = ResponseDTO.builder()
                .code(ResponseCode.DUPLICATE_EMAIL)
                .message(ResponseCode.DUPLICATE_EMAIL)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
    }

    public static ResponseEntity<ResponseDTO> duplicatedNickname() {
        ResponseDTO responseDTO = ResponseDTO.builder()
                .code(ResponseCode.DUPLICATE_NICKNAME)
                .message(ResponseCode.DUPLICATE_NICKNAME)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
    }
}
