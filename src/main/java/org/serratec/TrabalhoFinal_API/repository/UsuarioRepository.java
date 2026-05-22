package org.serratec.TrabalhoFinal_API.repository;

import java.util.UUID;

import org.serratec.TrabalhoFinal_API.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

}
