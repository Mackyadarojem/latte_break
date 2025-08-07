package com.example.latte_break.CTRL;

import com.example.latte_break.DAO.DAO_Login;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailSender {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    DAO_Login dao;

    public String sendEmail(String toEmail, int id) {
        // Generate a random 4-digit code
        int code = new Random().nextInt(9000) + 1000;

        String subject = "Your Verification Code";
        String body = "Your 4-digit verification code is: " + code;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("mackymejorada3@gmail.com");
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
        int res = dao.saveOTP(code, id);
        if (res > 0) {
            return "OTP Successfully in your email";
        } else {
            return "Error on sending OTP";
        }
    }

}
