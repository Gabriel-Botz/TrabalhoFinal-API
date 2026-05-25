package org.serratec.trabalho_final_api.repository;

import java.util.List;
import java.util.UUID;

import org.serratec.trabalho_final_api.domain.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeriesRepository extends JpaRepository<Series, UUID> {
    Series findByTitulo(String titulo);

    // o repository utiliza a classe domain Categoria como base e o DB
    List<Series> findByCategoriasId(Long id);
}
