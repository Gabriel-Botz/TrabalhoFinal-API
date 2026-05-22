package org.serratec.TrabalhoFinal_API.services;

import org.serratec.TrabalhoFinal_API.domain.Series;
import org.serratec.TrabalhoFinal_API.dto.responseDTO.SeriesResponseDTO;
import org.serratec.TrabalhoFinal_API.dto.rquestDTO.SeriesRequestDTO;
import org.serratec.TrabalhoFinal_API.repository.SeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class SeriesServices {

    @Autowired
    private SeriesRepository seriesRepository;

    //GET por todos
    public List<SeriesResponseDTO>  ListarTodasSeries() {
        List<Series> series = seriesRepository.findAll();
        List<SeriesResponseDTO> seriesDTO = new ArrayList<SeriesResponseDTO>();

        for(Series serie: series){
            seriesDTO.add(new SeriesResponseDTO(serie));
        }
        return seriesDTO;
    }

    //GET por titulo
    public SeriesResponseDTO ListarSeriePorTitulo(String titulo){
           Series  series = seriesRepository.findByTitulo(titulo);

           if(series == null){
               throw new RuntimeException("Série não encontrada!");
           }

         return new SeriesResponseDTO(series);
    }

    public SeriesResponseDTO inserirSeries(SeriesRequestDTO seriesRequest){
        Series series = seriesRepository.findByTitulo(seriesRequest.getTitulo());
        if(seriesRepository.findByTitulo(seriesRequest.getTitulo())!=null){
            throw new RuntimeException("Serie já existente!");
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

    public SeriesResponseDTO atualizarSeries(SeriesRequestDTO seriesRequest, Long id){
        Series series = seriesRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Serie não Encontrada"));

        series.setTitulo(seriesRequest.getTitulo());
        series.setDescricao(seriesRequest.getDescricao());
        series.setTemporadas(seriesRequest.getTemporadas());
        series.setEpisodios(series.getEpisodios());
        series.setDataLancamento(series.getDataLancamento());
        series.setNotaMedia(series.getNotaMedia());

        series = seriesRepository.save(series);
        return new SeriesResponseDTO(series);
    }

    public void removerSeries(Long id){
        Series series = seriesRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Serie não encontrada"));
        seriesRepository.delete(series);
    }
}
