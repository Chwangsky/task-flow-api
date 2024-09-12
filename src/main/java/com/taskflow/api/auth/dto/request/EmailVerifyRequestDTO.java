package com.taskflow.api.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmailVerifyRequestDTO {
    private String token;
    private Long userId;
}
