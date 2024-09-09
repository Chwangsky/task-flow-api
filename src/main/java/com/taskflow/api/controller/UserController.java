package com.taskflow.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class UserController {

    @GetMapping("/test")
    public String test() {
        log.info("hi!");
        return "HI!";
    }

    @GetMapping("/test2")
    public String test2() {
        log.info("hi!");
        return "HI!";
    }

}
