package com.taskflow.api.auth.dto.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import com.taskflow.api.common.ResponseCode;
import com.taskflow.api.common.dto.ResponseDTO;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class VerifyEmailResponseDTO extends ResponseDTO {

    public static ResponseEntity<VerifyEmailResponseDTO> ExpiredToken() {
        VerifyEmailResponseDTO dto = VerifyEmailResponseDTO.builder()
                .code(ResponseCode.TOKEN_EXPIRED)
                .message(ResponseCode.TOKEN_EXPIRED)
                .build();
        return ResponseEntity.status(HttpStatus.GONE).body(dto);
    }

    public static ResponseEntity<VerifyEmailResponseDTO> mismatchedToken() {
        VerifyEmailResponseDTO dto = VerifyEmailResponseDTO.builder()
                .code(ResponseCode.MISMATCH_TOKEN)
                .message(ResponseCode.MISMATCH_TOKEN)
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(dto);
    }

}
