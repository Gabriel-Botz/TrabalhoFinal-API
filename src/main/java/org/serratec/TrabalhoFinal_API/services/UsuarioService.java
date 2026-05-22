package org.serratec.TrabalhoFinal_API.services;

import java.util.List;
import java.util.UUID;

import org.serratec.TrabalhoFinal_API.domain.Usuario;
import org.serratec.TrabalhoFinal_API.dto.request.UsuarioRequestDTO;
import org.serratec.TrabalhoFinal_API.dto.response.UsuarioResponseDTO;
import org.serratec.TrabalhoFinal_API.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    /* --> Métodos GETs */

    @Transactional
    public List<UsuarioResponseDTO> listarTodos() {
        return repository.findAll().stream().map(UsuarioResponseDTO::toUsuarioResponseDTO).toList();
    }

    @Transactional
    public UsuarioResponseDTO buscar(UUID id) {
        return repository.findById(id).map(UsuarioResponseDTO::toUsuarioResponseDTO)
                .orElseThrow(() -> null); // -> new Exception("Usuario de ID '" + id + "' não encontrado")
    }

    /* Métodos POSTs */

    @Transactional
    public List<UsuarioResponseDTO> salvarList(List<UsuarioRequestDTO> requests) {

        List<Usuario> usuarios = requests.stream().map(UsuarioRequestDTO::toUsuario).toList();
        List<Usuario> salvos = repository.saveAll(usuarios);

        return salvos.stream().map(UsuarioResponseDTO::toUsuarioResponseDTO).toList();
    }

    @Transactional
    public UsuarioResponseDTO salvar(UsuarioRequestDTO request) {

        return UsuarioResponseDTO.toUsuarioResponseDTO(repository.save(request.toUsuario()));
    }

    /* Métodos PUT */

    @Transactional
    public UsuarioResponseDTO atualizar(UUID id, UsuarioRequestDTO request) {
        Usuario existe = repository.findById(id)
                .orElseThrow(() -> null); // -> new Exception("Usuario de ID '" + id +
                                          // "' não encontrado")

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
            // throw new Exception("Usuario de ID '" + id + "' não encontrado");

            repository.deleteById(id);
    }
}
