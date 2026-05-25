package org.serratec.trabalho_final_api.services;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.serratec.trabalho_final_api.domain.FotoUsuario;
import org.serratec.trabalho_final_api.domain.Usuario;
import org.serratec.trabalho_final_api.exception.RecursoNaoEncontradoException;
import org.serratec.trabalho_final_api.repository.FotoUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;

@Service
public class FotoUsuarioService {

    @Autowired
    private FotoUsuarioRepository repository;

    public FotoUsuario inserir(Usuario usuario, MultipartFile file) throws IOException {
        FotoUsuario foto = new FotoUsuario();
        foto.setUsuario(usuario);
        foto.setNome(file.getName());
        foto.setTipo(file.getContentType());
        foto.setDados(file.getBytes());

        return repository.save(foto);
    }

    @Transactional
    public FotoUsuario buscarPorIdUsuario(UUID id) {

        Usuario usuario = new Usuario();
        usuario.setId(id);
        Optional<FotoUsuario> foto = repository.findByUsuarioId(usuario);

        if (!foto.isPresent())
            throw new RecursoNaoEncontradoException("Foto do usuario não encontrado");

        return foto.get();
    }
}
