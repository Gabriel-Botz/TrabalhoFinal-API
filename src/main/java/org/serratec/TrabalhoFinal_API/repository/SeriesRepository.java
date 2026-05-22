package org.serratec.TrabalhoFinal_API.repository;


import org.serratec.TrabalhoFinal_API.domain.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeriesRepository extends JpaRepository<Series,Long> {
    Optional<Object> findById(UUID id);
    Series findByTitulo(String titulo);
}
