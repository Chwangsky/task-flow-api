package com.taskflow.api.auth.email;

import jakarta.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailProvider {

    private final JavaMailSender javaMailSender;

    private final String SUBJECT = "TASK FLOW 인증 메일입니다.";

    public boolean sendCertificationMail(String mail) {

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);

            String htmlContent = 

        } catch (Exception e) {
            e.printStackTrace(); // TODO: 예외처리추가
        }

    }

    private String getCertificationMessage(String cerficationNumber) {
        String certificationMessage = "";
        certificationMessage += "<h1 style='text-align: center;'>아래 버튼을 눌러 인증을 완료해 주세요</h1>";
    }
}
