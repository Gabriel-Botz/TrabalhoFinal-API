package org.serratec.TrabalhoFinal_API.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



import org.serratec.TrabalhoFinal_API.domain.AvaliacaoSerie;
import org.serratec.TrabalhoFinal_API.domain.Series;
import org.serratec.TrabalhoFinal_API.domain.Usuario;
import org.serratec.TrabalhoFinal_API.dto.request.AvaliacaoSerieRequestDTO;
import org.serratec.TrabalhoFinal_API.dto.response.AvaliacaoSerieResponseDTO;
import org.serratec.TrabalhoFinal_API.exception.RecursoNaoEncontradoException;
import org.serratec.TrabalhoFinal_API.repository.AvaliacaoSerieRepository;
import org.serratec.TrabalhoFinal_API.repository.SeriesRepository;
import org.serratec.TrabalhoFinal_API.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class AvaliacaoSerieService {

    @Autowired AvaliacaoSerieRepository avaliacaoSerieRepository;

    @Autowired UsuarioRepository usuarioRepository;

    @Autowired
    SeriesRepository serieRepository;

    public List <AvaliacaoSerieResponseDTO> findAll(){

        List<AvaliacaoSerie> avaliacoes = avaliacaoSerieRepository.findAll();

        List<AvaliacaoSerieResponseDTO> avaliacoesDTO = new ArrayList<>();

        for(AvaliacaoSerie avaliacao:avaliacoes){
            avaliacoesDTO.add(new AvaliacaoSerieResponseDTO(avaliacao));
        }
            return avaliacoesDTO;
    }

    public AvaliacaoSerieResponseDTO findById(UUID id){

        AvaliacaoSerie avaliacaoSerie = avaliacaoSerieRepository.findById(id)
            .orElseThrow(()-> new RecursoNaoEncontradoException("Serie não encontrada"));
         
        return new AvaliacaoSerieResponseDTO(avaliacaoSerie);
    }

    @Transactional
    public AvaliacaoSerieResponseDTO inserir (AvaliacaoSerieRequestDTO dto){

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
            .orElseThrow(()-> new RecursoNaoEncontradoException("Usuairio não encontrado"));

        Series serie = serieRepository.findById(dto.getSerieId())
            .orElseThrow(()-> new RecursoNaoEncontradoException("Serie não Encontrada"));
            
        AvaliacaoSerie avaliacaoSerie = new AvaliacaoSerie();
        avaliacaoSerie.setNota(dto.getNota());
        avaliacaoSerie.setComentario(dto.getComentario());
        avaliacaoSerie.setUsuario(usuario);
        avaliacaoSerie.setSeries(serie);
        
        avaliacaoSerie = avaliacaoSerieRepository.save(avaliacaoSerie);

        return new AvaliacaoSerieResponseDTO(avaliacaoSerie);
    }

    @Transactional
    public AvaliacaoSerieResponseDTO atualizar(UUID id, AvaliacaoSerieRequestDTO dto){

        AvaliacaoSerie avaliacaoSerie = avaliacaoSerieRepository.findById(id)
            .orElseThrow(()-> new RecursoNaoEncontradoException("Avaliação não encontrada"));

        avaliacaoSerie.setNota(dto.getNota());
        avaliacaoSerie.setComentario(dto.getComentario());
        
        avaliacaoSerie = avaliacaoSerieRepository.save(avaliacaoSerie);

        return new AvaliacaoSerieResponseDTO(avaliacaoSerie);
    }

    @Transactional
    public void deletar(UUID id){

        AvaliacaoSerie avaliacaoSerie = avaliacaoSerieRepository.findById(id)
            .orElseThrow(()-> new RecursoNaoEncontradoException("Avaliação não encontrada"));

        avaliacaoSerieRepository.delete(avaliacaoSerie);  
    }
}