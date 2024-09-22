package com.taskflow.api.auth.filter.jwt;

import java.util.Optional;

import com.taskflow.api.entity.UserEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtSubjectVO {

    private final String email;

    private final boolean isOAuth;

    public String toString() {
        if (isOAuth)
            return "GOOGLE:" + email;
        else
            return "LOCAL:" + email;
    }

    public static JwtSubjectVO fromEntity(UserEntity user) {
        return new JwtSubjectVO(user.getEmail(), user.isOAuth());
    }

    public static JwtSubjectVO fromString(String value) throws IllegalArgumentException {
        String[] splittedString = value.split(":");
        if (splittedString.length <= 1)
            return null;
        return JwtSubjectVO.builder()
                .email(splittedString[1])
                .isOAuth(splittedString[0].equals("GOOGLE"))
                .build();

    }
}
