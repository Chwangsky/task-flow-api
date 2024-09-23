package com.taskflow.api.auth.dto.response;

import org.springframework.http.ResponseEntity;

import com.taskflow.api.global.ResponseCode;
import com.taskflow.api.global.ResponseMessage;
import com.taskflow.api.global.dto.ResponseDTO;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class DuplicatedEmailResponseDTO extends ResponseDTO {
    private Boolean isDuplicated;

    public static ResponseEntity<DuplicatedEmailResponseDTO> duplicated(boolean isDuplicated) {
        DuplicatedEmailResponseDTO dto = DuplicatedEmailResponseDTO.builder()
                .isDuplicated(isDuplicated)
                .code(ResponseCode.SUCCESS)
                .message(ResponseMessage.SUCCESS)
                .build();
        return ResponseEntity.ok().body(dto);
    }

}
