package org.serratec.TrabalhoFinal_API.repository;

import java.util.UUID;

import org.serratec.TrabalhoFinal_API.domain.AvaliacaoFilme;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvaliacaoFilmeRepository extends JpaRepository <AvaliacaoFilme, UUID>{

}
