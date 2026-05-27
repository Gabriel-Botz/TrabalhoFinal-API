package org.serratec.trabalho_final_api.services;

import jakarta.transaction.Transactional;
import org.serratec.trabalho_final_api.domain.Series;
import org.serratec.trabalho_final_api.dto.response.SeriesRecomendadasResponseDTO;
import org.serratec.trabalho_final_api.exception.RecursoNaoEncontradoException;
import org.serratec.trabalho_final_api.repository.AvaliacaoSerieRepository;
import org.serratec.trabalho_final_api.repository.SeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class SeriesRecomendadasService {

  @Autowired
  private SeriesRepository seriesRepository;

  @Autowired
  private AvaliacaoSerieRepository avaliacaoSerieRepository;

  @Transactional
  public List<SeriesRecomendadasResponseDTO> recomendadas(UUID usuarioId){

      List<Long> categoriasFav = avaliacaoSerieRepository.buscarCatFavDoUsuario(usuarioId);

      if(categoriasFav == null || categoriasFav.isEmpty()){
         throw new RecursoNaoEncontradoException("Categoria favorita nao encontrado");
      }

      List<Series> seriesRecomendadas = seriesRepository.recomendarSerie(categoriasFav,usuarioId);

      return seriesRepository.recomendarSerie
              (categoriasFav,usuarioId)
              .stream()
              .map(SeriesRecomendadasResponseDTO::new)
              .toList();
  }
}
