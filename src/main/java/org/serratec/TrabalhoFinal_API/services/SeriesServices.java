package org.serratec.TrabalhoFinal_API.services;

import org.serratec.TrabalhoFinal_API.domain.Series;
import org.serratec.TrabalhoFinal_API.dto.response.SeriesResponseDTO;
import org.serratec.TrabalhoFinal_API.dto.request.SeriesRequestDTO;
import org.serratec.TrabalhoFinal_API.exception.RecursoNaoEncontradoException;
import org.serratec.TrabalhoFinal_API.repository.SeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SeriesServices {

    @Autowired
    private SeriesRepository seriesRepository;

    //GET por todos
    public List<SeriesResponseDTO> ListarTodasSeries() {
        List<Series> series = seriesRepository.findAll();
        List<SeriesResponseDTO> seriesDTO = new ArrayList<SeriesResponseDTO>();

        for(Series serie: series){
            seriesDTO.add(new SeriesResponseDTO(serie));
        }
        return seriesDTO;
    }

    public SeriesResponseDTO ListarSeriesPorId(@PathVariable UUID id) {
        Series series = (Series) seriesRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Serie não encontrada"));

        return new SeriesResponseDTO(series);
    }

    //GET por titulo
    public SeriesResponseDTO ListarSeriePorTitulo(String titulo){
           Series  series = seriesRepository.findByTitulo(titulo);

           if(series == null){
               throw new RecursoNaoEncontradoException("Série não encontrada!");
           }

         return new SeriesResponseDTO(series);
    }

    public SeriesResponseDTO inserirSeries(SeriesRequestDTO seriesRequest){
        Series series = seriesRepository.findByTitulo(seriesRequest.getTitulo());
        if(seriesRepository.findByTitulo(seriesRequest.getTitulo())!=null){
            throw new RecursoNaoEncontradoException("Serie já existente!");
        }
         Series serie =  new Series();
         serie.setTitulo(seriesRequest.getTitulo());
         serie.setDescricao(seriesRequest.getDescricao());
         serie.setTemporadas(seriesRequest.getTemporadas());
         serie.setEpisodios(series.getEpisodios());
         serie.setDataLancamento(series.getDataLancamento());
         serie.setNotaMedia(series.getNotaMedia());

         return new SeriesResponseDTO(seriesRepository.save(serie));
    }

    public SeriesResponseDTO atualizarSeries(SeriesRequestDTO seriesRequest, UUID id){
        Series series = (Series) seriesRepository.findById(id)
                .orElseThrow(()-> new RecursoNaoEncontradoException("Serie não Encontrada"));

        series.setTitulo(seriesRequest.getTitulo());
        series.setDescricao(seriesRequest.getDescricao());
        series.setTemporadas(seriesRequest.getTemporadas());
        series.setEpisodios(series.getEpisodios());
        series.setDataLancamento(series.getDataLancamento());
        series.setNotaMedia(series.getNotaMedia());

        series = seriesRepository.save(series);
        return new SeriesResponseDTO(series);
    }

    public void removerSeries(UUID id){
        Series series = (Series) seriesRepository.findById(id)
                .orElseThrow(()-> new RecursoNaoEncontradoException("Serie não encontrada"));
        seriesRepository.delete(series);
    }
}
