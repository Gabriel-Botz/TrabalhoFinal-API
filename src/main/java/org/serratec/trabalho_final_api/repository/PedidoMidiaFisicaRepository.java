package org.serratec.trabalho_final_api.repository;

import org.serratec.trabalho_final_api.domain.PedidoMidiaFisica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoMidiaFisicaRepository extends JpaRepository<PedidoMidiaFisica, Long>{
    
}
