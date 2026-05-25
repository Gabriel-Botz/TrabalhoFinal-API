package org.serratec.trabalho_final_api.services;

import java.util.List;

import io.mailtrap.client.MailtrapClient;
import io.mailtrap.model.request.emails.Address;
import io.mailtrap.model.request.emails.MailtrapMail;
import org.serratec.trabalho_final_api.domain.Usuario;
import org.serratec.trabalho_final_api.enumerated.TipoUsuario;
import org.serratec.trabalho_final_api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificacaoUsuarioService {

    @Autowired
    private MailtrapClient mailtrapClient;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // notifica um usuario expecifico o que foi realizado (ADMIN OU USER)
    public void avisarUsuario(Usuario usuario, String assunto, String mensagem) {
        MailtrapMail mail = MailtrapMail.builder()
                .from(new Address("hello@demomailtrap.co", "Serratec Flix"))
                .to(List.of(new Address(usuario.getEmail(), usuario.getNome())))
                .subject(assunto)
                .text(mensagem)
                .category("Cadastro de Usuário")
                .build();

        try {
            mailtrapClient.send(mail);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao disparar e-mail via SDK Mailtrap: " + e.getMessage(), e);
        }
    }

    // notifica a todos os administradores o que foi feito
    public void avisarVariosAdmin(String titulo, String mensagem) {
        List<String> adminEmails = usuarioRepository.findEmailsByTipoUsuario(TipoUsuario.ADMIN);

        List<Address> admin = adminEmails.stream()
                .map(email -> new Address(email))
                .toList();

        if (!admin.isEmpty()) {
            MailtrapMail mail = MailtrapMail.builder()
                    .from(new Address("hello@demomailtrap.co", "Serratec Flix"))
                    .to(admin)
                    .subject(titulo)
                    .text(mensagem)
                    .category("Aviso Administrativo")
                    .build();

            try {
                mailtrapClient.send(mail);
            } catch (Exception e) {
                throw new RuntimeException("Falha ao disparar e-mail em massa para Admins: " + e.getMessage(), e);
            }
        }
    }

    // notifica a todos os administradores o que foi feito
    public void avisarVariosUsuarios(String titulo, String mensagem) {
        List<String> userEmails = usuarioRepository.findEmailsByTipoUsuario(TipoUsuario.USER);

        List<Address> destinatarios = userEmails.stream()
                .map(email -> new Address(email))
                .toList();

        if (!destinatarios.isEmpty()) {
            MailtrapMail mail = MailtrapMail.builder()
                    .from(new Address("hello@demomailtrap.co", "Serratec Flix"))
                    .to(destinatarios)
                    .subject(titulo)
                    .text(mensagem)
                    .category("Aviso Geral")
                    .build();

            try {
                mailtrapClient.send(mail);
            } catch (Exception e) {
                throw new RuntimeException("Falha ao disparar e-mail em massa para Usuários: " + e.getMessage(), e);
            }
        }
    }
}