package org.serratec.trabalho_final_api.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.serratec.trabalho_final_api.domain.Categoria;
import org.serratec.trabalho_final_api.domain.Series;
import org.serratec.trabalho_final_api.dto.request.SeriesRequestDTO;
import org.serratec.trabalho_final_api.dto.response.SeriesResponseDTO;
import org.serratec.trabalho_final_api.dto.response.TmdbSerieResponseDTO;
import org.serratec.trabalho_final_api.exception.RecursoJaExistenteException;
import org.serratec.trabalho_final_api.exception.RecursoNaoEncontradoException;
import org.serratec.trabalho_final_api.repository.CategoriaRepository;
import org.serratec.trabalho_final_api.repository.SeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class SeriesServices {

    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private TmdbService tmdbService; // Injetando o serviço do TMDB

    // GET por todos
    public List<SeriesResponseDTO> ListarTodasSeries() {
        List<Series> series = seriesRepository.findAll();
        List<SeriesResponseDTO> seriesDTO = new ArrayList<>();

        for (Series serie : series) {
            seriesDTO.add(new SeriesResponseDTO(serie));
        }
        return seriesDTO;
    }

    @Transactional
    public SeriesResponseDTO ListarSeriesPorId(UUID id) {
        Series series = seriesRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Serie não encontrada"));

        return new SeriesResponseDTO(series);
    }

    // GET por titulo
    @Transactional
    public SeriesResponseDTO ListarSeriePorTitulo(String titulo) {
        Series series = seriesRepository.findByTitulo(titulo);

        if (series == null) {
            throw new RecursoNaoEncontradoException("Série não encontrada!");
        }

        return new SeriesResponseDTO(series);
    }

    @Transactional
    public SeriesResponseDTO inserirSeries(SeriesRequestDTO seriesRequest) {
        if (seriesRepository.findByTitulo(seriesRequest.getTitulo()) != null) {
            throw new RecursoJaExistenteException("Serie já existente!");
        }

        Series serie = new Series();
        serie.setTitulo(seriesRequest.getTitulo());
        serie.setDescricao(seriesRequest.getDescricao());
        serie.setTemporadas(seriesRequest.getTemporadas());

        // CORREÇÃO: Removido o setEpisodios que estava quebrado.
        // Mantido apenas o que é seguro do request que a equipe criou.
        serie.setDataLancamento(seriesRequest.getDataLancamento());
        serie.setNotaMedia(seriesRequest.getNotaMedia());

        return new SeriesResponseDTO(seriesRepository.save(serie));
    }

    @Transactional
    public SeriesResponseDTO vincularCategoria(UUID id, Long idCategoria) {
        Series series = seriesRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Serie não Encontrada"));

        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada"));

        series.getCategorias().add(categoria);
        return new SeriesResponseDTO(seriesRepository.save(series));
    }

    @Transactional
    public List<SeriesResponseDTO> buscarPorCategoria(Long idCategoria) {
        return seriesRepository.findByCategoriasId(idCategoria)
                .stream()
                .map(SeriesResponseDTO::new)
                .toList();
    }

    @Transactional
    public SeriesResponseDTO atualizarSeries(SeriesRequestDTO seriesRequest, UUID id) {
        Series series = seriesRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Serie não Encontrada"));

        series.setTitulo(seriesRequest.getTitulo());
        series.setDescricao(seriesRequest.getDescricao());
        series.setTemporadas(seriesRequest.getTemporadas());

        // CORREÇÃO: Removido o setEpisodios que estava quebrado
        series.setDataLancamento(seriesRequest.getDataLancamento());
        series.setNotaMedia(seriesRequest.getNotaMedia());

        series = seriesRepository.save(series);
        return new SeriesResponseDTO(series);
    }

    @Transactional
    public void removerSeries(UUID id) {
        Series series = seriesRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Serie não encontrada"));
        seriesRepository.delete(series);
    }

    // =========================================================================
    //                    MÉTODO DE BUSCA HÍBRIDA ADAPTADO
    // =========================================================================

    /**
     * Realiza a busca unificada de séries (Banco Local + API do TMDB)
     * Adaptado para usar apenas métodos confirmados do projeto.
     */
    @Transactional
    public List<SeriesResponseDTO> buscarCatalogoUnificado(String query) {
        List<SeriesResponseDTO> resultadoFinal = new ArrayList<>();

        // busca no banco local filtrando na memória para não depender de novos métodos no Repository
        List<SeriesResponseDTO> todasLocais = ListarTodasSeries();
        List<SeriesResponseDTO> locaisFiltradas = todasLocais.stream()
                .filter(s -> s.getTitulo() != null && s.getTitulo().toLowerCase().contains(query.toLowerCase()))
                .toList();

        resultadoFinal.addAll(locaisFiltradas);

        // busca na API Externa do TMDB
        TmdbSerieResponseDTO seriesExternas = tmdbService.pesquisarSeriesNoTmdb(query);
        if (seriesExternas != null && seriesExternas.getResultados() != null) {
            for (TmdbSerieResponseDTO.TmdbSerieItem itemExterno : seriesExternas.getResultados()) {

                //evita duplicar se o título do TMDB já existir no banco local
                boolean jaExisteLocalmente = locaisFiltradas.stream()
                        .anyMatch(s -> itemExterno.getName() != null && itemExterno.getName().equalsIgnoreCase(s.getTitulo()));

                if (!jaExisteLocalmente) {
                    //metodo paraSerieResponseDTO() monta o objeto usando apenas setTitulo, setDescricao e setDataLancamento
                    SeriesResponseDTO dtoExterno = itemExterno.paraSerieResponseDTO();
                    resultadoFinal.add(dtoExterno);
                }
            }
        }

        return resultadoFinal;
    }
}