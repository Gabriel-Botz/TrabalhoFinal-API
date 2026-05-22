package org.serratec.TrabalhoFinal_API.services;

import java.util.List;
import java.util.UUID;

import org.serratec.TrabalhoFinal_API.domain.Usuario;
import org.serratec.TrabalhoFinal_API.dto.request.UsuarioRequestDTO;
import org.serratec.TrabalhoFinal_API.dto.response.UsuarioResponseDTO;
import org.serratec.TrabalhoFinal_API.exception.DataConflictException;
import org.serratec.TrabalhoFinal_API.exception.RecursoNaoEncontradoException;
import org.serratec.TrabalhoFinal_API.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /* --> Métodos GETs */

    @Transactional
    public List<UsuarioResponseDTO> listarTodos() {
        return repository.findAll().stream().map(UsuarioResponseDTO::toUsuarioResponseDTO).toList();
    }

    @Transactional
    public UsuarioResponseDTO buscar(UUID id) {
        return repository.findById(id).map(UsuarioResponseDTO::toUsuarioResponseDTO)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario de ID '" + id + "' não encontrado"));
    }

    /* Métodos POSTs */

    @Transactional
    public List<UsuarioResponseDTO> salvarList(List<UsuarioRequestDTO> requests) {

        List<Usuario> Usuario = requests.stream().map(UsuarioRequestDTO::toUsuario).toList();
        List<Usuario> salvos = repository.saveAll(Usuario);

        return salvos.stream().map(UsuarioResponseDTO::toUsuarioResponseDTO).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public UsuarioResponseDTO salvar(UsuarioRequestDTO request) {

        if (repository.existsByUsername(request.username()))
            throw new DataConflictException("O nome de usuário '" + request.username() + "' já está em uso.");

        if (repository.existsByEmail(request.email()))
            throw new DataConflictException("O e-mail '" + request.email() + "' já está cadastrado.");

        Usuario usuario = request.toUsuario();
        String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);

        return UsuarioResponseDTO.toUsuarioResponseDTO(repository.save(usuario));
    }

    /* Métodos PUT */

    @Transactional
    public UsuarioResponseDTO atualizar(UUID id, UsuarioRequestDTO request) {
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
        if (!repository.existsById(id))
            throw new RecursoNaoEncontradoException("Usuario de ID '" + id + "' não encontrado");

        repository.deleteById(id);
    }
}
