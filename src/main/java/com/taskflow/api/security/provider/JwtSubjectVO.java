package com.taskflow.api.security.provider;

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

    public static JwtSubjectVO fromString(String value) throws IllegalArgumentException {
        String[] splittedString = value.split(":");
        if (splittedString.length <= 1)
            throw new IllegalArgumentException();
        return JwtSubjectVO.builder()
                .email(splittedString[1])
                .isOAuth(splittedString[0].equals("GOOGLE"))
                .build();

    }
}
