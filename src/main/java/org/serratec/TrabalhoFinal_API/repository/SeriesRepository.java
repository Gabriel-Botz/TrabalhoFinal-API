package org.serratec.TrabalhoFinal_API.repository;


import org.serratec.TrabalhoFinal_API.domain.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeriesRepository extends JpaRepository<Series,Long> {
    Series findByTitulo(String titulo);
}
