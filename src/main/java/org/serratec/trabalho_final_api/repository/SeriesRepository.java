package org.serratec.trabalho_final_api.repository;

import java.util.List;
import java.util.UUID;
import org.serratec.trabalho_final_api.domain.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SeriesRepository extends JpaRepository<Series, UUID> {
    Series findByTitulo(String titulo);

    // o repository utiliza a classe domain Categoria como base e o DB
    List<Series> findByCategoriasId(UUID idCategoria);

   @Query("SELECT DISTINCT sr FROM Series sr " +
           " JOIN sr.categorias ct " +
           " WHERE ct.id IN :categorias " +
           " AND sr.id " +
           " NOT IN (SELECT av.series.id " +
           "         FROM AvaliacaoSerie av " +
           " WHERE av.usuario.id = :usuarioId)" +
           " ORDER BY sr.notaMedia DESC"
   )
 List<Series> recomendarSerie(@Param("categorias") List<UUID> categorias,
                              @Param("usuarioId") UUID usuarioId);
}
