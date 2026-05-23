package org.serratec.trabalho_final_api.services;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.serratec.trabalho_final_api.domain.AvaliacaoFilme;
import org.serratec.trabalho_final_api.domain.Filme;
import org.serratec.trabalho_final_api.domain.Usuario;
import org.serratec.trabalho_final_api.dto.request.AvaliacaoFilmeRequestDTO;
import org.serratec.trabalho_final_api.dto.response.AvaliacaoFilmeResponseDTO;
import org.serratec.trabalho_final_api.exception.RecursoNaoEncontradoException;
import org.serratec.trabalho_final_api.repository.AvaliacaoFilmeRepository;
import org.serratec.trabalho_final_api.repository.FilmeRepository;
import org.serratec.trabalho_final_api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class AvaliacaoFilmeService {


    @Autowired
    AvaliacaoFilmeRepository avaliacaoFilmeRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    FilmeRepository filmeRepository;

    public List<AvaliacaoFilmeResponseDTO> findAll(){

        List<AvaliacaoFilme> avaliacoes = avaliacaoFilmeRepository.findAll();

        List<AvaliacaoFilmeResponseDTO> avaliacoesDTO = new ArrayList<>();

        for(AvaliacaoFilme avaliacao:avaliacoes){
            avaliacoesDTO.add(new AvaliacaoFilmeResponseDTO(avaliacao));

        }
            return avaliacoesDTO;
    }


    public AvaliacaoFilmeResponseDTO findById(UUID id){

        AvaliacaoFilme avaliacaoFilme = avaliacaoFilmeRepository.findById(id)
            .orElseThrow(()-> new RuntimeException("Avaliação não encontrada"));

        return new AvaliacaoFilmeResponseDTO(avaliacaoFilme);
    }

    @Transactional
    public AvaliacaoFilmeResponseDTO inserir (AvaliacaoFilmeRequestDTO dto){

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
            .orElseThrow(()-> new RuntimeException("Usuairio não encontrado"));

        Filme filme = filmeRepository.findById(dto.getFilmeId())
            .orElseThrow(()-> new RuntimeException("Filme não encontrado"));
    
        
        AvaliacaoFilme avaliacaoFilme = new AvaliacaoFilme();
        avaliacaoFilme.setNota(dto.getNota());
        avaliacaoFilme.setComentario(dto.getComentario());
        avaliacaoFilme.setUsuario(usuario);
        avaliacaoFilme.setFilme(filme);

        avaliacaoFilme = avaliacaoFilmeRepository.save(avaliacaoFilme);

        return new AvaliacaoFilmeResponseDTO(avaliacaoFilme);
    }

    @Transactional
    public AvaliacaoFilmeResponseDTO atualizar (UUID id, AvaliacaoFilmeRequestDTO dto){
        
        AvaliacaoFilme avaliacaoFilme = avaliacaoFilmeRepository.findById(id)
            .orElseThrow(()-> new RuntimeException("Avaliação não encontrada"));

        avaliacaoFilme.setNota(dto.getNota());
        avaliacaoFilme.setComentario(dto.getComentario());
        
        avaliacaoFilme = avaliacaoFilmeRepository.save(avaliacaoFilme);

        return new AvaliacaoFilmeResponseDTO(avaliacaoFilme);
    }
    
    @Transactional
    public void deletar(UUID id){

        AvaliacaoFilme avaliacaoFilme = avaliacaoFilmeRepository.findById(id)
            .orElseThrow(()-> new RecursoNaoEncontradoException("Avaliação não encontrada"));

        avaliacaoFilmeRepository.delete(avaliacaoFilme);    
    }


}
