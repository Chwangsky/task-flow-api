package com.taskflow.api.auth.dto.request;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmailVerifyRequestDTO {
    @NotNull
    private Long userId;
    @NotNull
    private String token;

}
