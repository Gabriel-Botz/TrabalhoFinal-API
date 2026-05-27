package org.serratec.trabalho_final_api.repository;

import java.util.List;
import java.util.UUID;

import org.serratec.trabalho_final_api.domain.AvaliacaoSerie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AvaliacaoSerieRepository extends JpaRepository<AvaliacaoSerie, UUID> {

    @Query(" SELECT ct.id FROM AvaliacaoSerie av " +
             " JOIN av.series sr " +
             " JOIN sr.categorias ct " +
             " WHERE av.usuario.id=:usuarioId " +
             " AND av.nota >=8 " +
             " GROUP BY ct.id " +
             " ORDER BY AVG(av.nota) DESC")
   List<UUID> buscarCatFavDoUsuario(UUID usuarioId);
}
