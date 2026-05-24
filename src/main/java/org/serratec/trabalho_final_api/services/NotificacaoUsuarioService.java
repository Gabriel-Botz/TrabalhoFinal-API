package org.serratec.trabalho_final_api.services;

import java.util.List;

import org.serratec.trabalho_final_api.config.MailService;
import org.serratec.trabalho_final_api.domain.Usuario;
import org.serratec.trabalho_final_api.enumerated.TipoUsuario;
import org.serratec.trabalho_final_api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class NotificacaoUsuarioService {
    @Autowired
    private MailService mailService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public void avisarUsuario(Usuario usuario, String titulo, String mensagem) {
        mailService.sendEmail(usuario.getEmail(), titulo, mensagem);
    }

    public void avisarAdmin(String titulo, String mensagem) {
        List<String> adminEmails = usuarioRepository.findEmailsByTipoUsuario(TipoUsuario.ADMIN);

        adminEmails.forEach(
                email -> mailService.sendEmail(email, titulo, mensagem));
    }
}
