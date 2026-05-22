package org.serratec.TrabalhoFinal_API.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "usuario")
@EqualsAndHashCode(of = "id")
public class Usuarios {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String senha;

    @Column(name = "foto_perfil")
    private String fotoPerfil;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    // @OneToMany
    // @JoinColumn(name = "avaliacaoFilme_id", referencedColumnName = "id")
    // private List<AvaliacaoFilme> avaliacoesFilmes = new ArrayList<>();

    // @OneToMany
    // @JoinColumn(name = "avaliacaoSerie_id", referencedColumnName = "id")
    // private List<AvaliacaoSerie> avaliacoesSeries = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "listaFavoritos_id", referencedColumnName = "id")
    private List<ListaFavoritos> listasFavoritos = new ArrayList<>();

    @PrePersist
    protected void onCriar() {
        this.dataCriacao = LocalDateTime.now();
    }
}
