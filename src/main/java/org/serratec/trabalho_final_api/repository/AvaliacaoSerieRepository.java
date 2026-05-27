package org.serratec.trabalho_final_api.repository;

import java.util.List;
import java.util.UUID;

import org.serratec.trabalho_final_api.domain.AvaliacaoSerie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvaliacaoSerieRepository extends JpaRepository<AvaliacaoSerie, UUID> {
    List<AvaliacaoSerie> findAllByOrderByNotaDesc();
}
