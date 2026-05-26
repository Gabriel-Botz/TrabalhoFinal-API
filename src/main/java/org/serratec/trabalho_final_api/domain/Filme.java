package org.serratec.trabalho_final_api.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.serratec.trabalho_final_api.enumerated.ClassificacaoIndicativa;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "filmes")
public class Filme {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tmdb_id")
    private Long tmdbId;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "duracao")
    private Integer duracao;

    @Column(name = "data_lancamento")
    private LocalDate dataLancamento;

    @Column(name = "nota_media")
    private Double notaMedia = 0.0;

    @Enumerated(EnumType.STRING)
    private ClassificacaoIndicativa classificacaoIndicativa;

    @ManyToMany
    @JoinTable(name = "filme_categoria", joinColumns = @JoinColumn(name = "filme_id"), inverseJoinColumns = @JoinColumn(name = "categoria_id"))
    private List<Categoria> categorias = new ArrayList<>();

    @OneToMany(mappedBy = "filme")
    @JsonManagedReference
    private List<AvaliacaoFilme> avaliacoes = new ArrayList<>();

    @ManyToMany(mappedBy = "filmes")
    @JsonBackReference
    private List<ListaFavoritos> listaFavoritos = new ArrayList<>();

    public String getTitulo() {
        return this.titulo;
    }
}
