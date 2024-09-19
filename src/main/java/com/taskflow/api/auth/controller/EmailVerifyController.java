package com.taskflow.api.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskflow.api.auth.dto.request.EmailVerifyRequestDTO;
import com.taskflow.api.auth.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping
@RequiredArgsConstructor
public class EmailVerifyController {

    private final EmailService emailService;

    @PostMapping("/email-verify")
    public String postMethodName(@ModelAttribute EmailVerifyRequestDTO emailVerifyRequestDTO, Model model) {

        log.info("Received DTO: {}", emailVerifyRequestDTO);
        String alert = emailService.verifyToken(emailVerifyRequestDTO);
        model.addAttribute("dto", alert);

        return "EmailVerify";
    }

}
