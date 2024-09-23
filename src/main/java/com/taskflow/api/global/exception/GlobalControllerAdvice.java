package com.taskflow.api.global.exception;

import jakarta.persistence.PersistenceException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.taskflow.api.global.ResponseCode;
import com.taskflow.api.global.ResponseMessage;
import com.taskflow.api.global.dto.ResponseDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<ResponseDTO> handleConstraintViolationException(
            MethodArgumentNotValidException ex) {

        log.warn("서버에 이상이 발생했습니다.");

        ResponseDTO body = ResponseDTO.builder()
                .code(ResponseCode.DATABASE_ERROR)
                .message(ResponseMessage.DATABASE_ERROR)
                .build();

        return ResponseEntity.badRequest().body(body);
    }

}