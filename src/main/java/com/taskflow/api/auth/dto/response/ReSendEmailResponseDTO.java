package com.taskflow.api.auth.dto.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.taskflow.api.global.ResponseCode;
import com.taskflow.api.global.ResponseMessage;
import com.taskflow.api.global.dto.ResponseDTO;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ReSendEmailResponseDTO extends ResponseDTO {

    public static ResponseEntity<ReSendEmailResponseDTO> alreadyVerifiedEmail() {
        ReSendEmailResponseDTO dto = ReSendEmailResponseDTO.builder()
                .code(ResponseCode.ALREADY_VERIFIED_ACCOUNT)
                .message(ResponseMessage.ALREADY_VERIFIED_ACCOUNT)
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(dto);
    }

    public static ResponseEntity<ReSendEmailResponseDTO> noSuchUser() {
        ReSendEmailResponseDTO dto = ReSendEmailResponseDTO.builder()
                .code(ResponseCode.NO_PERMISSION)
                .message(ResponseMessage.NO_PERMISSION)
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(dto);
    }

    public static ResponseEntity<ReSendEmailResponseDTO> success() {
        ReSendEmailResponseDTO dto = ReSendEmailResponseDTO.builder()
                .code(ResponseCode.SUCCESS)
                .message(ResponseMessage.SUCCESS)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

}
