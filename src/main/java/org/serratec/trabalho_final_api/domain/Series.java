package org.serratec.trabalho_final_api.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "series")
public class Series {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "titulo", nullable = false)
    private String titulo;
    @Column(name = "descricao", nullable = false)
    private String descricao;
    @Column(name = "temporadas", nullable = false)
    private Integer temporadas;
    @Column(name = "episodios", nullable = false)
    private Integer episodios;
    @Column(name = "data_lancamento", nullable = false)
    private LocalDate dataLancamento;
    @Column(name = "nota_media", nullable = false)
    private Double notaMedia;

    @OneToMany(mappedBy = "series")
    private List<AvaliacaoSerie> avaliacaoSerie;

    @ManyToMany
    @JoinTable(name = "serie_categoria", joinColumns = @JoinColumn(name = "id_series"), inverseJoinColumns = @JoinColumn(name = "categoria_id"))
    private List<Categoria> categorias;

}
