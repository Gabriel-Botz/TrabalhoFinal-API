package org.serratec.trabalho_final_api.repository;

import java.util.Optional;
import java.util.UUID;

import org.serratec.trabalho_final_api.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findbyUserName(String username);

}
