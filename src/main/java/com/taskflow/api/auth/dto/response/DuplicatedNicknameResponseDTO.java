package com.taskflow.api.auth.dto.response;

import org.springframework.http.ResponseEntity;

import com.taskflow.api.global.ResponseCode;
import com.taskflow.api.global.ResponseMessage;
import com.taskflow.api.global.dto.ResponseDTO;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class DuplicatedNicknameResponseDTO extends ResponseDTO {
    private boolean isDuplicated;

    public static ResponseEntity<DuplicatedNicknameResponseDTO> duplicated(boolean isDuplicated) {
        DuplicatedNicknameResponseDTO dto = DuplicatedNicknameResponseDTO.builder()
                .isDuplicated(isDuplicated)
                .code(ResponseCode.SUCCESS)
                .message(ResponseMessage.SUCCESS)
                .build();
        return ResponseEntity.ok().body(dto);
    }
}
