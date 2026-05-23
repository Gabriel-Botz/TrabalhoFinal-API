package org.serratec.trabalho_final_api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailConfig {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(String para, String assunto, String texto) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("serraflix@gmail.com");
        message.setTo(para);
        message.setSubject(assunto);
        message.setText(texto);

        javaMailSender.send(message);
    }

}
