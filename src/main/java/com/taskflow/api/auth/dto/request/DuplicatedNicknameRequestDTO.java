package com.taskflow.api.auth.dto.request;

import jakarta.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DuplicatedNicknameRequestDTO {
    @NotEmpty
    private String nickname;

}
