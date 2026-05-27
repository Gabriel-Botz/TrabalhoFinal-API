package org.serratec.trabalho_final_api.services;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import org.serratec.trabalho_final_api.domain.Usuario;
import org.serratec.trabalho_final_api.dto.request.UsuarioRequestDTO;
import org.serratec.trabalho_final_api.dto.response.UsuarioResponseDTO;
import org.serratec.trabalho_final_api.enumerated.TipoUsuario;
import org.serratec.trabalho_final_api.exception.AcessoNegadoException;
import org.serratec.trabalho_final_api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private NotificacaoUsuarioService notificacao;

    @Autowired
    private PermissaoService permissao;

    /* --> Métodos GETs */

    @Transactional
    public List<UsuarioResponseDTO> listarTodos() {

        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin)
            throw new AcessoNegadoException(
                    "Acesso negado: Apenas administradores podem listar todos os usuários.");

        return repository.findAll().stream().map(UsuarioResponseDTO::toUsuarioResponseDTO).toList();
    }

    @Transactional
    public UsuarioResponseDTO buscar(UUID id) {

        Usuario usuario = permissao.validarObter(id);
        return UsuarioResponseDTO.toUsuarioResponseDTO(usuario);

    }

    @Transactional
    public List<UsuarioResponseDTO> buscarPorUsername(String username) {
        return repository.findByUsernameEndsWith(username.toUpperCase()).stream()
                .map(UsuarioResponseDTO::toUsuarioResponseDTO).toList();
    }

    @Transactional
    public List<UsuarioResponseDTO> buscarPorTipoUsuario(String tipo) {
        TipoUsuario tipoEnum = TipoUsuario.valueOf(tipo.toUpperCase());
        return repository.findByTipoUsuario(
                tipoEnum).stream()
                .map(UsuarioResponseDTO::toUsuarioResponseDTO).toList();
    }

    /* Métodos POSTs */

    @Transactional
    public List<UsuarioResponseDTO> salvarList(List<UsuarioRequestDTO> requests) {

        List<Usuario> usuarios = requests.stream()
                .map(request -> {
                    Usuario user = request.toUsuario();
                    user.setSenha(passwordEncoder.encode(user.getSenha()));
                    return user;
                }).toList();

        List<Usuario> salvos = repository.saveAll(usuarios);

        for (Usuario usuario : salvos) {

            try {
                StringBuilder mensagem = new StringBuilder();
                StringBuilder mensagemAdm = new StringBuilder();

                mensagem // Método de mensagem para USER
                        .append("Cadastro do usuario '")
                        .append(usuario.getUsername())
                        .append("' realizado com sucesso,")
                        .append("às '")
                        .append(usuario.getDataCriacao())
                        .append("'")
                        .append("\n Obrigado por se registrar em nosso sistema serratecFlix XD");

                mensagemAdm // método de mensagem para o ADMIN
                        .append("Avido do Sistem: Um novo usuário foi cadastrado.\n")
                        .append("➤ Id: '").append(usuario.getId()).append("'\n")
                        .append("➤ Nome: '").append(usuario.getNome()).append("'\n")
                        .append("➤ Username: '").append(usuario.getUsername()).append("'\n")
                        .append("➤ Email: '").append(usuario.getEmail()).append("'\n")
                        .append("➤ ")
                        .append("➤ Data da Criação: '").append(usuario.getDataCriacao()).append("'\n")
                        .append("➤ Tipo de Usuário: '").append(usuario.getTipoUsuario()).append("'\n");

                // Chamando os métodos e passando as informações para o envio de e-mail
                notificacao.avisarUsuario(usuario, "Cadastro Realizado com Sucesso", mensagem.toString());
                notificacao.avisarVariosAdmin("Cadastro Realizado com Sucesso", mensagem.toString());

            } catch (Exception e) {
                System.err.println("Falha ao enviar notificação por e-mail para o usuário " + usuario.getUsername()
                        + ": " + e.getMessage());
            }
        }

        return salvos.stream().map(UsuarioResponseDTO::toUsuarioResponseDTO).toList();
    }

    @Transactional
    public UsuarioResponseDTO salvar(UsuarioRequestDTO request) {

        Usuario usuario = request.toUsuario();
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        Usuario usuarioSalvo = repository.save(usuario);
        UsuarioResponseDTO response = UsuarioResponseDTO.toUsuarioResponseDTO(usuarioSalvo);

        try {
            // criando a mensagem com o StringBuilder para ficar mais organizado
            StringBuilder mensagem = new StringBuilder();
            StringBuilder mensagemAdm = new StringBuilder();

            mensagem // Método de mensagem para USER
                    .append("Cadastro do usuario '")
                    .append(usuario.getUsername())
                    .append("' realizado com sucesso,")
                    .append("às '")
                    .append(usuario.getDataCriacao())
                    .append("'\n")
                    .append("Obrigado por se registrar em nosso sistema serratecFlix XD");

            mensagemAdm // método de mensagem para o ADMIN
                    .append("Avido do Sistem: Um novo usuário foi cadastrado.")
                    .append("➤ Id: '").append(usuario.getId()).append("'\n")
                    .append("➤ Nome: '").append(usuario.getNome()).append("'\n")
                    .append("➤ Username: '").append(usuario.getUsername()).append("'\n")
                    .append("➤ Email: '").append(usuario.getEmail()).append("'\n")
                    .append("➤ ")
                    .append("➤ Data da Criação: '").append(usuario.getDataCriacao()).append("'\n")
                    .append("➤ Tipo de Usuário: '").append(usuario.getTipoUsuario()).append("'\n");

            // Chamando os métodos e passando as informações para o envio de e-mail
            notificacao.avisarUsuario(usuario, "Cadastro Realizado com Sucesso",
                    mensagem.toString());
            notificacao.avisarVariosAdmin("Cadastro de usuário '" + usuario.getUsername()
                    + "'' realizado com Sucesso!",
                    mensagem.toString());
        } catch (

        Exception e) {
            System.err.println("Erro ao tentar enviar e-mails de notificação: " +
                    e.getMessage());
        }

        return response;

    }

    /* Métodos PUT */

    @Transactional
    public UsuarioResponseDTO atualizar(UUID id, UsuarioRequestDTO request) {

        // valida a permissao e retorna o id --> Olhar casse PermissaoService
        Usuario existe = permissao.validarObter(id);

        StringBuilder mensagem = new StringBuilder();
        mensagem.append("Alerta! \nAlteração nos dados do usuario: \n'").append(existe.getUsername()).append("'");

        if (request.nome() != null && !request.nome().isBlank()) {
            existe.setNome(request.nome());
            mensagem.append("➤ Nome realizado com sucesso!\n");
        }

        if (request.email() != null && !request.email().isBlank()) {
            existe.setEmail(request.email());
            mensagem.append("➤ Email realizado com sucesso!\n");
        }

        if (request.username() != null && !request.username().isBlank()) {
            existe.setUsername(request.username());
            mensagem.append("➤ Username realizado com sucesso!\n");
        }

        if (request.senha() != null && !request.senha().isBlank()) {
            existe.setSenha(passwordEncoder.encode(request.senha()));
            mensagem.append("➤ Senha realizado com sucesso!\n");
        }

        notificacao.avisarUsuario(existe,
                ("Alteração de dados do Usuário às" + LocalTime.now(ZoneId.of("America/Sao_Paulo"))),
                mensagem.toString());
        return UsuarioResponseDTO.toUsuarioResponseDTO(repository.save(existe));
    }

    @Transactional
    public void excluir(UUID id) {

        Usuario existe = permissao.validarObter(id);

        StringBuilder mensagem = new StringBuilder();
        mensagem.append("Olá, ").append(existe.getUsername()).append("!")
                .append("\nEstamos passando para confirmar que a sua conta foi encerrada.")
                .append("\nSentiremos sua falta! Se decidir voltar no futuro, as portas estarão sempre abertas.");

        notificacao.avisarUsuario(existe, "Confirmação de exclusão de conta", mensagem.toString());

        repository.delete(existe);
    }

}
