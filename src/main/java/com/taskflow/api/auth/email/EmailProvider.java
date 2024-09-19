package com.taskflow.api.auth.email;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailProvider {

    private final JavaMailSender javaMailSender;

    private final String SUBJECT = "TASK FLOW 인증 메일입니다.";

    @Async
    public void sendCertificationMail(String mail, String certificationNumber, Long userId) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

            messageHelper.setTo(mail);
            messageHelper.setSubject(SUBJECT);
            messageHelper.setText(getCertificationMessage(certificationNumber, userId), true); // HTML 형식의 내용 설정

            javaMailSender.send(message);
            return;
        } catch (Exception e) {
            e.printStackTrace(); // TODO: 예외처리 추가
            return;
        }
    }

    private String getCertificationMessage(String certificationNumber, Long userId) {
        return "<div style='background-color: #000000; color: #ffffff; padding: 30px; text-align: center; font-family: Arial, sans-serif;'>"
                + "<h1 style='margin-bottom: 20px;'>아래 버튼을 눌러 인증을 완료해 주세요</h1>"
                + "<form action='http://localhost:9090/email-verify' method='POST' style='margin-bottom: 40px;'>"
                + "<input type='hidden' name='userId' value='" + String.valueOf(userId) + "'>"
                + "<input type='hidden' name='token' value='" + certificationNumber + "'>"
                + "<button type='submit' style='padding: 15px 25px; font-size: 18px; font-weight: bold; color: #ffffff; background-color: #79EDFF; text-decoration: none; border-radius: 8px;'>"
                + "인증하기"
                + "</button>"
                + "</form>"
                + "</div>";
    }

}
