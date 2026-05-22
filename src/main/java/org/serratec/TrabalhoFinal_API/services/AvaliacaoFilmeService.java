package org.serratec.TrabalhoFinal_API.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.naming.NameNotFoundException;

import org.serratec.TrabalhoFinal_API.domain.AvaliacaoFilme;
import org.serratec.TrabalhoFinal_API.dto.Request.AvaliacaoFilmeDTORequest;
import org.serratec.TrabalhoFinal_API.dto.Response.AvaliacaoFilmeDTOResponse;
import org.serratec.TrabalhoFinal_API.repository.AvaliacaoFilmeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class AvaliacaoFilmeService {


    @Autowired AvaliacaoFilmeRepository avaliacaoFilmeRepository;

    @Autowired UsuarioRepository usuarioRepository;

    @Autowired FilmeRepository filmeRepository;

    public List<AvaliacaoFilmeDTOResponse> findAll(){

        List<AvaliacaoFilme> avaliacoes = avaliacaoFilmeRepository.findAll();

        List<AvaliacaoFilmeDTOResponse> avaliacoesDTO = new ArrayList<>();

        for(AvaliacaoFilme avaliacao:avaliacoes){
            avaliacoesDTO.add(new AvaliacaoFilmeDTOResponse(avaliacao));

        }
            return avaliacoesDTO;
    }


    public AvaliacaoFilmeDTOResponse findById(UUID id){

        AvaliacaoFilme avaliacaoFilme = avaliacaoFilmeRepository.findById(id)
            .orElseThrow(()-> new NotFoundExeption("Avaliação não encontrada"));

        return new AvaliacaoFilmeDTOResponse(avaliacaoFilme);    
    }

    @Transactional
    public AvaliacaoFilmeDTOResponse inserir (AvaliacaoFilmeDTORequest dto){

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
            .orElseThrow(()-> new NotFoundExeption("Usuairio não encontrado"));

        Filme filme = filmeRepository.findById(dto.getFilmeId())
            .orElseThrow(()-> new NotFoundExeption("Filme não encontrado"));   
    
        
        AvaliacaoFilme avaliacaoFilme = new AvaliacaoFilme();
        avaliacaoFilme.setNota(dto.getNota());
        avaliacaoFilme.setComentario(dto.getComentario());
        avaliacaoFilme.setUsuario(usuario);
        avaliacaoFilme.setFilme(filme);

        avaliacaoFilme = avaliacaoFilmeRepository.save(avaliacaoFilme);

        return new AvaliacaoFilmeDTOResponse(avaliacaoFilme);
    }

    @Transactional
    public AvaliacaoFilmeDTOResponse atualizar (UUID id, AvaliacaoFilmeDTORequest dto){
        
        AvaliacaoFilme avaliacaoFilme = avaliacaoFilmeRepository.findById(id)
            .orElseThrow(()-> new NotFoundExeption("Avaliação não encontrada"));

        avaliacaoFilme.setNota(dto.getNota());
        avaliacaoFilme.setComentario(dto.getComentario());
        
        avaliacaoFilme = avaliacaoFilmeRepository.save(avaliacaoFilme);

        return new AvaliacaoFilmeDTOResponse(avaliacaoFilme);
    }
    
    @Transactional
    public void deletar(UUID id){

        AvaliacaoFilme avaliacaoFilme = avaliacaoFilmeRepository.findById(id)
            .orElseThrow(()-> new NotFoundException("Avaliação não encontrada"));

        avaliacaoFilmeRepository.delete(avaliacaoFilme);    
    }


}
