package com.taskflow.api.auth.dto.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import com.taskflow.api.common.ResponseCode;
import com.taskflow.api.common.ResponseMessage;
import com.taskflow.api.common.dto.ResponseDTO;
import com.taskflow.api.entity.UserEntity;

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
}
