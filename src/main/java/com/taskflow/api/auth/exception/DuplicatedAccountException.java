package com.taskflow.api.auth.exception;

public class DuplicatedAccountException extends RuntimeException {
    public DuplicatedAccountException(String message) {
        super(message);
    }

    public DuplicatedAccountException(String message, Throwable cause) {
        super(message, cause);
    }
}
