package com.taskflow.api.auth.exception;

public class DuplicatedNicknameException extends RuntimeException {
    public DuplicatedNicknameException(String message) {
        super(message);
    }

    public DuplicatedNicknameException(String message, Throwable cause) {
        super(message, cause);
    }
}
