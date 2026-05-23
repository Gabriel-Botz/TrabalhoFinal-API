package org.serratec.trabalho_final_api.services;

import java.util.List;
import java.util.UUID;

import org.serratec.trabalho_final_api.domain.Usuario;
import org.serratec.trabalho_final_api.dto.request.UsuarioRequestDTO;
import org.serratec.trabalho_final_api.dto.response.UsuarioResponseDTO;
import org.serratec.trabalho_final_api.exception.AcessoNegadoException;
import org.serratec.trabalho_final_api.exception.RecursoNaoEncontradoException;
import org.serratec.trabalho_final_api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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

    private void permissao(UUID id) {
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();

        if (autenticacao == null || !autenticacao.isAuthenticated())
            throw new AcessoNegadoException("Usuário não autorizado!");

        String username = autenticacao.getName();

        boolean admin = autenticacao.getAuthorities().stream().anyMatch(
                authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (admin)
            return;

        Usuario usuarioDonoDoRecurso = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario de ID '" + id + "' não encontrado"));

        if (!usuarioDonoDoRecurso.getUsername().equals(username)) {
            throw new AcessoNegadoException(
                    "Você não tem permissão para acessar ou modificar os dados de outro usuário.");
        }

    }

    /* --> Métodos GETs */

    @Transactional
    public List<UsuarioResponseDTO> listarTodos() {
        return repository.findAll().stream().map(UsuarioResponseDTO::toUsuarioResponseDTO).toList();
    }

    @Transactional
    public UsuarioResponseDTO buscar(UUID id) {
        permissao(id);
        return repository.findById(id).map(UsuarioResponseDTO::toUsuarioResponseDTO)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario de ID '" + id + "' não encontrado"));
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
        return salvos.stream().map(UsuarioResponseDTO::toUsuarioResponseDTO).toList();
    }

    @Transactional
    public UsuarioResponseDTO salvar(UsuarioRequestDTO request) {

        Usuario usuario = request.toUsuario();
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        return UsuarioResponseDTO.toUsuarioResponseDTO(repository.save(request.toUsuario()));
    }

    /* Métodos PUT */

    @Transactional
    public UsuarioResponseDTO atualizar(UUID id, UsuarioRequestDTO request) {
        permissao(id);
        Usuario existe = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario de ID '" + id + "' não encontrado"));

        if (request.nome() != null && !request.nome().isBlank())
            existe.setNome(request.nome());

        if (request.email() != null && !request.email().isBlank())
            existe.setEmail(request.email());

        if (request.username() != null && !request.username().isBlank())
            existe.setUsername(request.username());

        if (request.senha() != null && !request.senha().isBlank())
            existe.setSenha(request.senha());

        return UsuarioResponseDTO.toUsuarioResponseDTO(repository.save(existe));
    }

    @Transactional
    public void excluir(UUID id) {
        Usuario existe = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario de ID '" + id + "' não encontrado"));

        permissao(id);
        repository.delete(existe);
    }
}
