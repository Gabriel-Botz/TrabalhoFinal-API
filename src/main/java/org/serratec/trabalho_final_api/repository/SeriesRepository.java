package org.serratec.trabalho_final_api.repository;

import java.util.List;
import java.util.UUID;

import org.serratec.trabalho_final_api.domain.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeriesRepository extends JpaRepository<Series, UUID> {
    Series findByTitulo(String titulo);
    List<Series> findByCategoria_id(Long Long categoriaId);
}
