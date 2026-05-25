package org.serratec.trabalho_final_api.services;

import java.util.List;

import org.serratec.trabalho_final_api.domain.Usuario;
import org.serratec.trabalho_final_api.enumerated.TipoUsuario;
import org.serratec.trabalho_final_api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificacaoUsuarioService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Value("${spring.mail.username}")
    private String remetente;

    // notifica um usuario expecifico o que foi realizado (ADMIN OU USER)
    public void avisarUsuario(Usuario usuario, String assunto, String mensagem) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(remetente);
        mail.setTo(usuario.getEmail());
        mail.setSubject(assunto);
        mail.setText(mensagem);

        try {
            mailSender.send(mail);
            System.out.println("E-mail real enviado com sucesso para: " + usuario.getEmail());
        } catch (Exception e) {
            throw new RuntimeException("Falha ao disparar e-mail via Gmail: " + e.getMessage(), e);
        }
    }

    // notifica a todos os administradores o que foi feito
    public void avisarVariosAdmin(String titulo, String mensagem) {
        List<String> adminEmails = usuarioRepository.findEmailsByTipoUsuario(TipoUsuario.ADMIN);

        if (!adminEmails.isEmpty()) {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(remetente);
            mail.setTo(adminEmails.toArray(String[]::new));
            mail.setSubject(titulo);
            mail.setText(mensagem);

            try {
                mailSender.send(mail);
                System.out.println("E-mail em massa enviado para os Administradores.");
            } catch (Exception e) {
                throw new RuntimeException("Falha ao disparar e-mail em massa para Admins: " + e.getMessage(), e);
            }
        }
    }

    // notifica a todos os usuários comuns o que foi feito
    public void avisarVariosUsuarios(String titulo, String mensagem) {
        List<String> userEmails = usuarioRepository.findEmailsByTipoUsuario(TipoUsuario.USER);

        if (!userEmails.isEmpty()) {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(remetente);
            mail.setTo(userEmails.toArray(String[]::new));
            mail.setSubject(titulo);
            mail.setText(mensagem);

            try {
                mailSender.send(mail);
                System.out.println("E-mail em massa enviado para os Usuários comuns.");
            } catch (Exception e) {
                throw new RuntimeException("Falha ao disparar e-mail em massa para Usuários: " + e.getMessage(), e);
            }
        }
    }
}