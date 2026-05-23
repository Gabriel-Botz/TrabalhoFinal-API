package org.serratec.TrabalhoFinal_API.repository;

import org.serratec.TrabalhoFinal_API.domain.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long>{

    Optional<Object> findById(UUID categoriaId);
}