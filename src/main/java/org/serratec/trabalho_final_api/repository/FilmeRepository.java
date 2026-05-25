package org.serratec.trabalho_final_api.repository;

import java.util.List;
import java.util.UUID;
import org.serratec.trabalho_final_api.domain.Filme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmeRepository extends JpaRepository<Filme, UUID> {
    List<Filme> findByCategorias_Id(Long categoriaId);

    // busca filmes por nome ignorando maiúsculas/minúsculas :)
    List<Filme> findByTituloContainingIgnoreCase(String titulo);
}