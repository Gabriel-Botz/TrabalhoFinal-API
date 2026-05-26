package org.serratec.trabalho_final_api.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

    @NotNull(message = "O status de privacidade é obrigatório")
    private Boolean privada;

    @NotNull(message = "A data de criação é obrigatória")
    private LocalDate dataCriacao;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "lista_favoritos_filme", joinColumns = @JoinColumn(name = "lista_id"), inverseJoinColumns = @JoinColumn(name = "filme_id"))
    @JsonManagedReference
    private List<Filme> filmes;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "lista_favoritos_series", joinColumns = @JoinColumn(name = "lista_id"), inverseJoinColumns = @JoinColumn(name = "series_id"))
    @JsonManagedReference
    private List<Series> series;

    @ManyToOne
    @JsonBackReference
    private Usuario usuario;
}
