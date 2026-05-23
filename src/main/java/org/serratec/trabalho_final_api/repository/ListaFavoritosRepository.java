package org.serratec.trabalho_final_api.repository;

import java.util.UUID;

import org.serratec.trabalho_final_api.domain.ListaFavoritos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListaFavoritosRepository extends JpaRepository<ListaFavoritos, UUID> {

}
