package org.serratec.TrabalhoFinal_API.services;

import java.util.List;
import java.util.UUID;

import org.serratec.TrabalhoFinal_API.domain.Usuarios;
import org.serratec.TrabalhoFinal_API.dto.request.UsuariosRequestDTO;
import org.serratec.TrabalhoFinal_API.dto.response.UsuariosResponseDTO;
import org.serratec.TrabalhoFinal_API.exception.DataConflictException;
import org.serratec.TrabalhoFinal_API.exception.RecursoNaoEncontradoException;
import org.serratec.TrabalhoFinal_API.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuariosService {

    @Autowired
    private UsuariosRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /* --> Métodos GETs */

    @Transactional
    public List<UsuariosResponseDTO> listarTodos() {
        return repository.findAll().stream().map(UsuariosResponseDTO::toUsuarioResponseDTO).toList();
    }

    @Transactional
    public UsuariosResponseDTO buscar(UUID id) {
        return repository.findById(id).map(UsuariosResponseDTO::toUsuarioResponseDTO)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario de ID '" + id + "' não encontrado"));
    }

    /* Métodos POSTs */

    @Transactional
    public List<UsuariosResponseDTO> salvarList(List<UsuariosRequestDTO> requests) {

        List<Usuarios> usuarios = requests.stream().map(UsuariosRequestDTO::toUsuario).toList();
        List<Usuarios> salvos = repository.saveAll(usuarios);

        return salvos.stream().map(UsuariosResponseDTO::toUsuarioResponseDTO).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public UsuariosResponseDTO salvar(UsuariosRequestDTO request) {

        if (repository.existsByUsername(request.username()))
            throw new DataConflictException("O nome de usuário '" + request.username() + "' já está em uso.");

        if (repository.existsByEmail(request.email()))
            throw new DataConflictException("O e-mail '" + request.email() + "' já está cadastrado.");

        Usuarios usuario = request.toUsuario();
        String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);

        return UsuariosResponseDTO.toUsuarioResponseDTO(repository.save(usuario));
    }

    /* Métodos PUT */

    @Transactional
    public UsuariosResponseDTO atualizar(UUID id, UsuariosRequestDTO request) {
        Usuarios existe = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario de ID '" + id + "' não encontrado"));

        if (request.nome() != null && !request.nome().isBlank())
            existe.setNome(request.nome());

        if (request.email() != null && !request.email().isBlank())
            existe.setEmail(request.email());

        if (request.username() != null && !request.username().isBlank())
            existe.setUsername(request.username());

        if (request.senha() != null && !request.senha().isBlank())
            existe.setSenha(request.senha());

        return UsuariosResponseDTO.toUsuarioResponseDTO(repository.save(existe));
    }

    @Transactional
    public void excluir(UUID id) {
        if (!repository.existsById(id))
            throw new RecursoNaoEncontradoException("Usuario de ID '" + id + "' não encontrado");

        repository.deleteById(id);
    }
}
