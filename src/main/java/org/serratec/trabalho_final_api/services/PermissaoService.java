package org.serratec.trabalho_final_api.services;

import java.util.UUID;

import org.serratec.trabalho_final_api.domain.Usuario;
import org.serratec.trabalho_final_api.exception.AcessoNegadoException;
import org.serratec.trabalho_final_api.exception.RecursoNaoEncontradoException;
import org.serratec.trabalho_final_api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class PermissaoService {

    @Autowired
    private UsuarioRepository repository;

    public Usuario validarObter(UUID id) {
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();

        if (autenticacao == null || !autenticacao.isAuthenticated()) // verifica a autenciacao do usuario
            throw new AcessoNegadoException("Usuário não autorizado!");

        Usuario usuario = repository.findById(id) // verifica se o usuario existe
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario de ID '" + id + "' não encontrado"));

        String username = autenticacao.getName(); // pega o username do usuario

        boolean admin = autenticacao.getAuthorities().stream().anyMatch( // verifica se é ou não ADMIN
                authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (admin) // se for admin, retorna os dados dele e a autorização geral do CRUD
            return usuario;

        // se não for admin e nem o dono, usuario tem limitações
        if (!admin && !usuario.getUsername().equals(username)) {
            throw new AcessoNegadoException(
                    "Você não tem permissão para acessar ou modificar os dados de outro usuário.");
        }

        return usuario;

    }
}
