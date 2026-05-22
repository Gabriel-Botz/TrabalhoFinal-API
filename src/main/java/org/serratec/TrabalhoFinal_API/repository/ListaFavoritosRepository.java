package org.serratec.TrabalhoFinal_API.repository;

import java.util.UUID;

import org.serratec.TrabalhoFinal_API.domain.ListaFavoritos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListaFavoritosRepository extends JpaRepository<ListaFavoritos, UUID> {

}
