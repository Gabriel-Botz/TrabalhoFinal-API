package org.serratec.TrabalhoFinal_API.repository;

import java.util.UUID;

import org.serratec.TrabalhoFinal_API.domain.AvaliacaoSerie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvaliacaoSerieRepository extends JpaRepository<AvaliacaoSerie, UUID> {

}
