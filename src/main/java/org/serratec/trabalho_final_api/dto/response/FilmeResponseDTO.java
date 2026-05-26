package org.serratec.trabalho_final_api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import lombok.Setter;
import org.serratec.trabalho_final_api.domain.Filme;
import org.serratec.trabalho_final_api.enumerated.ClassificacaoIndicativa;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Schema(description = "DTO para resposta de filme")
public class FilmeResponseDTO {

    public FilmeResponseDTO(Filme filme) {
        this.id = filme.getId();
        this.titulo = filme.getTitulo();
        this.descricao = filme.getDescricao();
        this.duracao = filme.getDuracao();
        this.dataLancamento = filme.getDataLancamento();
        this.notaMedia = filme.getNotaMedia();
        this.classificacaoIndicativa = filme.getClassificacaoIndicativa();
        this.tmdbId = filme.getTmdbId();
    }

    private UUID id;

    private Long tmdbId;

    @Schema(description = "Titulo do filme", example = "O Poderoso Chefão")
    private String titulo;

    @Schema(description = "Descrição do filme", example = "Um drama épico sobre uma família mafiosa")
    private String descricao;

    @Schema(description = "Duração do filme em minutos")
    private Integer duracao;

    @Schema(description = "Data de lançamento do filme")
    private LocalDate dataLancamento;

    @Schema(description = "Nota média do filme")
    private Double notaMedia;

    @Schema(description = "Classificação indicativa do filme")
    private ClassificacaoIndicativa classificacaoIndicativa;

}
