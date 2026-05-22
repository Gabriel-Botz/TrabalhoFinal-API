package org.serratec.TrabalhoFinal_API.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



import org.serratec.TrabalhoFinal_API.domain.AvaliacaoSerie;
import org.serratec.TrabalhoFinal_API.dto.request.AvaliacaoSerieDTORequest;
import org.serratec.TrabalhoFinal_API.dto.response.AvaliacaoSerieDTOResponse;
import org.serratec.TrabalhoFinal_API.repository.AvaliacaoSerieRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class AvaliacaoSerieService {

    @Autowired AvaliacaoSerieRepository avaliacaoSerieRepository;

    @Autowired UsuarioRepository usuarioRepository;

    @Autowired SerieRepository serieRepository;

    public List <AvaliacaoSerieDTOResponse> findAll(){

        List<AvaliacaoSerie> avaliacoes = avaliacaoSerieRepository.findAll();

        List<AvaliacaoSerieDTOResponse> avaliacoesDTO = new ArrayList<>();

        for(AvaliacaoSerie avaliacao:avaliacoes){
            avaliacoesDTO.add(new AvaliacaoSerieDTOResponse(avaliacao));
        }
            return avaliacoesDTO;
    }

    public AvaliacaoSerieDTOResponse findById(UUID id){

        AvaliacaoSerie avaliacaoSerie = avaliacaoSerieRepository.findById(id)
            .orElseThrow(()-> new NotFoundExeption("Serie não encontrada"));
         
        return new AvaliacaoSerieDTOResponse(avaliacaoSerie);  
    }

    @Transactional
    public AvaliacaoSerieDTOResponse inserir (AvaliacaoSerieDTORequest dto){

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
            .orElseThrow(()-> new NotFoundExeption("Usuairio não encontrado"));

        Serie serie = serieRepository.findById(dto.getSerieId())
            .orElseThrow(()-> new NotFoundExeption("Serie não Encontrada"));
            
        AvaliacaoSerie avaliacaoSerie = new AvaliacaoSerie();
        avaliacaoSerie.setNota(dto.getNota());
        avaliacaoSerie.setComentario(dto.getComentario());
        avaliacaoSerie.setUsuario(usuario);
        avaliacaoSerie.setSerie(serie);
        
        avaliacaoSerie = avaliacaoSerieRepository.save(avaliacaoSerie);

        return new AvaliacaoSerieDTOResponse(avaliacaoSerie);
    }

    @Transactional
    public AvaliacaoSerieDTOResponse atualizar(UUID id, AvaliacaoSerieDTORequest dto){

        AvaliacaoSerie avaliacaoSerie = avaliacaoSerieRepository.findById(id)
            .orElseThrow(()-> new NotFoundException("Avaliação não encontrada"));

        avaliacaoSerie.setNota(dto.getNota());
        avaliacaoSerie.setComentario(dto.getComentario());
        
        avaliacaoSerie = avaliacaoSerieRepository.save(avaliacaoSerie);

        return new AvaliacaoSerieDTOResponse(avaliacaoSerie);
    }

    @Transactional
    public void deletar(UUID id){

        AvaliacaoSerie avaliacaoSerie = avaliacaoSerieRepository.findById(id)
            .orElseThrow(()-> NotFoundExeption("Avaliação não encontrada"));

        avaliacaoSerieRepository.delete(avaliacaoSerie);    
    }
}
