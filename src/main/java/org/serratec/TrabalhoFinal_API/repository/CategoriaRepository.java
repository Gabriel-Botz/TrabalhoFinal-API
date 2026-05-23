package org.serratec.TrabalhoFinal_API.repository;

import java.util.Optional;

import org.serratec.TrabalhoFinal_API.domain.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long>{

    Optional<Categoria> findById(Long categoriaId);
}