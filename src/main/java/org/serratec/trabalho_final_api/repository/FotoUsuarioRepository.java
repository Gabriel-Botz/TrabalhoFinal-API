package org.serratec.trabalho_final_api.repository;

import java.util.Optional;
import java.util.UUID;

import org.serratec.trabalho_final_api.domain.FotoUsuario;
import org.serratec.trabalho_final_api.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FotoUsuarioRepository extends JpaRepository<FotoUsuario, UUID> {

    public Optional<FotoUsuario> findByUsuarioId(Usuario usuario);

}
