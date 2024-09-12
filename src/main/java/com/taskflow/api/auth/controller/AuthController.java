package com.taskflow.api.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController("/api/v1/")
public class AuthController {

    @GetMapping("/sign-up")
    public String test() {
        log.info("hi!");
        return "HI!";
    }

    @GetMapping("/login-in")
    public String test2() {
        log.info("hi!");
        return "HI!";
    }

}
