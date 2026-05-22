package org.serratec.TrabalhoFinal_API.repository;

import org.hibernate.validator.constraints.UUID;
import org.serratec.TrabalhoFinal_API.domain.Filme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmeRepository extends JpaRepository<Filme, UUID> {


}
