package org.serratec.TrabalhoFinal_API.domain;

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
@RequiredArgsConstructor
@Table(name = "series")
public class Series {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "titulo",nullable = false)
    private String titulo;
    @Column(name = "descricao",nullable = false)
    private String descricao;
    @Column(name = "temporadas",nullable = false)
    private Integer temporadas;
    @Column(name = "episodios",nullable = false)
    private Integer episodios;
    @Column(name = "dataLancamento",nullable = false)
    private LocalDate dataLancamento;
    @Column(name = "notaMedia",nullable = false)
    private Double notaMedia;

    @OneToMany(mappedBy = "series")
    private List<AvaliacaoSerie> avaliacaoSerie;

    @ManyToMany
    @JoinTable(name="serie_categoria",
            joinColumns =  @JoinColumn(name = "id_series"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id"))
    private List<Categoria> categorias;

}
