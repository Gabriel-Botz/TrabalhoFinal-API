package org.serratec.trabalho_final_api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.serratec.trabalho_final_api.domain.Usuario;
import org.serratec.trabalho_final_api.enumerated.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByUsername(String username);

    @Query("SELECT u.email FROM Usuario u WHERE u.tipoUsuario = :tipo")
    List<String> findEmailsByTipoUsuario(@Param("tipo") TipoUsuario tipo);

    // @Query("SELECT u FROM Usuario u WHERE u.username Like%?1")
    List<Usuario> findByUsernameEndsWith(String username);

    @Query("SELECT u FROM Usuario u WHERE u.tipoUsuario = :tipo")
    List<Usuario> findByTipoUsername(@Param("tipo") TipoUsuario tipo);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsernameIn(List<String> usernames);

    boolean existsByEmailIn(List<String> emails);

}
