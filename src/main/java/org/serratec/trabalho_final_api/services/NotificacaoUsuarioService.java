package org.serratec.trabalho_final_api.services;

import java.util.List;

import org.serratec.trabalho_final_api.config.MailService;
import org.serratec.trabalho_final_api.domain.Usuario;
import org.serratec.trabalho_final_api.enumerated.TipoUsuario;
import org.serratec.trabalho_final_api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificacaoUsuarioService {

    @Autowired
    private MailService mailService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // notifica um usuario expecifico o que foi realizado (ADMIN OU USER)
    public void avisarUsuario(Usuario usuario, String titulo, String mensagem) {
        mailService.sendEmail(usuario.getEmail(), titulo, mensagem);
    }

    // notifica a todos os administradores o que foi feito
    public void avisarVariosAdmin(String titulo, String mensagem) {
        List<String> adminEmails = usuarioRepository.findEmailsByTipoUsuario(TipoUsuario.ADMIN);

        adminEmails.forEach(
                email -> mailService.sendEmail(email, titulo, mensagem));
    }

    // notifica a todos os administradores o que foi feito
    public void avisarVariosUsuarios(String titulo, String mensagem) {
        List<String> adminEmails = usuarioRepository.findEmailsByTipoUsuario(TipoUsuario.USER);

        adminEmails.forEach(
                email -> mailService.sendEmail(email, titulo, mensagem));
    }
}
