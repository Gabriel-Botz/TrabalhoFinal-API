package org.serratec.trabalho_final_api.domain;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "lista_favoritos")
public class ListaFavoritos {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @NotNull(message = "O nome da lista não pode ser nulo")
    @Size(max = 120, message = "O nome da lista deve ter no máximo 120 caracteres")
    private String nomeLista;
    
    @NotBlank(message = "O status de privacidade é obrigatório")
    private Boolean privada;
    
    @NotBlank(message = "A data de criação é obrigatória")
    private LocalDate dataCriacao;
    
}
