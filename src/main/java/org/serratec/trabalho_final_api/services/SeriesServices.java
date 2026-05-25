package org.serratec.trabalho_final_api.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.transaction.Transactional;
import org.serratec.trabalho_final_api.domain.Categoria;
import org.serratec.trabalho_final_api.domain.Series;
import org.serratec.trabalho_final_api.dto.request.SeriesRequestDTO;
import org.serratec.trabalho_final_api.dto.response.SeriesResponseDTO;
import org.serratec.trabalho_final_api.exception.RecursoNaoEncontradoException;
import org.serratec.trabalho_final_api.repository.CategoriaRepository;
import org.serratec.trabalho_final_api.repository.SeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class SeriesServices {

    @Autowired
    private SeriesRepository seriesRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

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
    public SeriesResponseDTO ListarSeriesPorId(@PathVariable UUID id) {
        Series series =  seriesRepository.findById(id)
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
        Series series = seriesRepository.findByTitulo(seriesRequest.getTitulo());
        if (seriesRepository.findByTitulo(seriesRequest.getTitulo()) != null) {
            throw new RecursoNaoEncontradoException("Serie já existente!");
        }
        Series serie = new Series();
        serie.setTitulo(seriesRequest.getTitulo());
        serie.setDescricao(seriesRequest.getDescricao());
        serie.setTemporadas(seriesRequest.getTemporadas());
        serie.setEpisodios(series.getEpisodios());
        serie.setDataLancamento(series.getDataLancamento());
        serie.setNotaMedia(series.getNotaMedia());

        return new SeriesResponseDTO(seriesRepository.save(serie));
    }

    @Transactional
    public SeriesResponseDTO vincularCategoria (UUID id,Long idCategoria) {

        Series series =  seriesRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Serie não Encontrada"));

        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada"));

        series.getCategorias().add(categoria);
        return new SeriesResponseDTO(seriesRepository.save(series));

    }

    @Transactional
    public List<SeriesResponseDTO> buscarPorCategoria(Long idCategoria){
        return seriesRepository.findByCategoria_id(idCategoria)
                .stream()
                .map(SeriesResponseDTO::new)
                .toList();
    }

    @Transactional
    public SeriesResponseDTO atualizarSeries(SeriesRequestDTO seriesRequest, UUID id) {
        Series series =  seriesRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Serie não Encontrada"));

        series.setTitulo(seriesRequest.getTitulo());
        series.setDescricao(seriesRequest.getDescricao());
        series.setTemporadas(seriesRequest.getTemporadas());
        series.setEpisodios(series.getEpisodios());
        series.setDataLancamento(series.getDataLancamento());
        series.setNotaMedia(series.getNotaMedia());

        series = seriesRepository.save(series);
        return new SeriesResponseDTO(series);
    }

    @Transactional
    public void removerSeries(UUID id) {
        Series series =  seriesRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Serie não encontrada"));
        seriesRepository.delete(series);
    }
}
