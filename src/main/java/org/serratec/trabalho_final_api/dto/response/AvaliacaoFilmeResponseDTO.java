package org.serratec.trabalho_final_api.dto.response;

import java.time.LocalDate;
import java.util.UUID;

import org.serratec.trabalho_final_api.domain.AvaliacaoFilme;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvaliacaoFilmeResponseDTO {

    @Schema(description = "ID de avaliação de filme", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Nome do usuário que realizou a avaliação", example = "Gabriel Martineli")
    private String nomeUsuario;

    @Schema(description = "Nome do filme avaliado", example = "Gran Turismo: De Jogador a Corredor")
    private String nomeFilme;

    @Schema(description = "Nota atribuída ao filme", example = "9.5")
    private Double nota;

    @Schema(description = "Comentário feito pelo usuário", example = "Filme incrível, com ótima trilha sonora e roteiro.")
    private String comentario;

    @Schema(description = "Data da avaliação", example = "2026-05-25")
    private LocalDate dataAvaliacao;

    public AvaliacaoFilmeResponseDTO(AvaliacaoFilme avaliacaoFilme) {
        this.id = avaliacaoFilme.getId();
        this.nomeUsuario = avaliacaoFilme.getUsuario().getNome();
        this.nomeFilme = avaliacaoFilme.getFilme().getTitulo();
        this.nota = avaliacaoFilme.getNota();
        this.comentario = avaliacaoFilme.getComentario();
        this.dataAvaliacao = avaliacaoFilme.getDataAvaliacao();
    }

}
