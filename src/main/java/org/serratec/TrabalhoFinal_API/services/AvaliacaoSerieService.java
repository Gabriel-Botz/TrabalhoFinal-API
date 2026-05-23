package org.serratec.TrabalhoFinal_API.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



import org.serratec.TrabalhoFinal_API.domain.AvaliacaoSerie;
import org.serratec.TrabalhoFinal_API.domain.Usuario;
import org.serratec.TrabalhoFinal_API.dto.response.AvaliacaoSerieDTO;
import org.serratec.TrabalhoFinal_API.exception.ErroResposta.ResourceNotFoundException;
import org.serratec.TrabalhoFinal_API.exception.RecursoNaoEncontradoException;
import org.serratec.TrabalhoFinal_API.repository.AvaliacaoSerieRepository;
import org.serratec.TrabalhoFinal_API.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class AvaliacaoSerieService {

    @Autowired AvaliacaoSerieRepository avaliacaoSerieRepository;

    @Autowired UsuarioRepository usuarioRepository;

    @Autowired SerieRepository serieRepository;

    public List <AvaliacaoSerieDTO> findAll(){

        List<AvaliacaoSerie> avaliacoes = avaliacaoSerieRepository.findAll();

        List<AvaliacaoSerieDTO> avaliacoesDTO = new ArrayList<>();

        for(AvaliacaoSerie avaliacao:avaliacoes){
            avaliacoesDTO.add(new AvaliacaoSerieDTO(avaliacao));
        }
            return avaliacoesDTO;
    }

    public AvaliacaoSerieDTO findById(UUID id){

        AvaliacaoSerie avaliacaoSerie = avaliacaoSerieRepository.findById(id)
            .orElseThrow(()-> new RecursoNaoEncontradoException("Serie não encontrada"));
         
        return new AvaliacaoSerieDTO(avaliacaoSerie);
    }

    @Transactional
    public AvaliacaoSerieDTO inserir (org.serratec.TrabalhoFinal_API.dto.request.AvaliacaoSerieDTO dto){

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
            .orElseThrow(()-> new ResourceNotFoundException("Usuairio não encontrado"));

        Serie serie = serieRepository.findById(dto.getSerieId())
            .orElseThrow(()-> new RecursoNaoEncontradoException("Serie não Encontrada"));
            
        AvaliacaoSerie avaliacaoSerie = new AvaliacaoSerie();
        avaliacaoSerie.setNota(dto.getNota());
        avaliacaoSerie.setComentario(dto.getComentario());
        avaliacaoSerie.setUsuario(usuario);
        avaliacaoSerie.setSerie(serie);
        
        avaliacaoSerie = avaliacaoSerieRepository.save(avaliacaoSerie);

        return new AvaliacaoSerieDTO(avaliacaoSerie);
    }

    @Transactional
    public AvaliacaoSerieDTO atualizar(UUID id, org.serratec.TrabalhoFinal_API.dto.request.AvaliacaoSerieDTO dto){

        AvaliacaoSerie avaliacaoSerie = avaliacaoSerieRepository.findById(id)
            .orElseThrow(()-> new RecursoNaoEncontradoException("Avaliação não encontrada"));

        avaliacaoSerie.setNota(dto.getNota());
        avaliacaoSerie.setComentario(dto.getComentario());
        
        avaliacaoSerie = avaliacaoSerieRepository.save(avaliacaoSerie);

        return new AvaliacaoSerieDTO(avaliacaoSerie);
    }

    @Transactional
    public void deletar(UUID id){

        AvaliacaoSerie avaliacaoSerie = avaliacaoSerieRepository.findById(id)
            .orElseThrow(()-> new RecursoNaoEncontradoException ("Avaliação não encontrada"));

        avaliacaoSerieRepository.delete(avaliacaoSerie);  
    }
}
