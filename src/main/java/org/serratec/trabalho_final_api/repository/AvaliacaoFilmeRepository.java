package org.serratec.trabalho_final_api.repository;

import java.util.UUID;

import org.serratec.trabalho_final_api.domain.AvaliacaoFilme;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvaliacaoFilmeRepository extends JpaRepository <AvaliacaoFilme, UUID>{

}
