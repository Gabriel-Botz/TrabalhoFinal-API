package org.serratec.TrabalhoFinal_API.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private List<ListaFavoritos> listaFavoritos = new ArrayList<>();

}
