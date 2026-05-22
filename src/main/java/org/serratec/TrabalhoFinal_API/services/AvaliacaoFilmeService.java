package org.serratec.TrabalhoFinal_API.services;


import jakarta.transaction.Transactional;
import org.serratec.TrabalhoFinal_API.domain.AvaliacaoFilme;
import org.serratec.TrabalhoFinal_API.domain.Filme;
import org.serratec.TrabalhoFinal_API.domain.Usuario;
import org.serratec.TrabalhoFinal_API.dto.response.AvaliacaoFilmeDTO;
import org.serratec.TrabalhoFinal_API.exceptions.ResourceNotFoundException;
import org.serratec.TrabalhoFinal_API.repository.AvaliacaoFilmeRepository;
import org.serratec.TrabalhoFinal_API.repository.FilmeRepository;
import org.serratec.TrabalhoFinal_API.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AvaliacaoFilmeService {


    @Autowired
    AvaliacaoFilmeRepository avaliacaoFilmeRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    FilmeRepository filmeRepository;

    public List<AvaliacaoFilmeDTO> findAll(){

        List<AvaliacaoFilme> avaliacoes = avaliacaoFilmeRepository.findAll();

        List<AvaliacaoFilmeDTO> avaliacoesDTO = new ArrayList<>();

        for(AvaliacaoFilme avaliacao:avaliacoes){
            avaliacoesDTO.add(new AvaliacaoFilmeDTO(avaliacao));

        }
            return avaliacoesDTO;
    }


    public AvaliacaoFilmeDTO findById(UUID id){

        AvaliacaoFilme avaliacaoFilme = avaliacaoFilmeRepository.findById(id)
            .orElseThrow(()-> new RuntimeException("Avaliação não encontrada"));

        return new AvaliacaoFilmeDTO(avaliacaoFilme);
    }

    @Transactional
    public AvaliacaoFilmeDTO inserir (org.serratec.TrabalhoFinal_API.dto.request.AvaliacaoFilmeDTO dto){

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

        return new AvaliacaoFilmeDTO(avaliacaoFilme);
    }

    @Transactional
    public AvaliacaoFilmeDTO atualizar (UUID id, org.serratec.TrabalhoFinal_API.dto.request.AvaliacaoFilmeDTO dto){
        
        AvaliacaoFilme avaliacaoFilme = avaliacaoFilmeRepository.findById(id)
            .orElseThrow(()-> new RuntimeException("Avaliação não encontrada"));

        avaliacaoFilme.setNota(dto.getNota());
        avaliacaoFilme.setComentario(dto.getComentario());
        
        avaliacaoFilme = avaliacaoFilmeRepository.save(avaliacaoFilme);

        return new AvaliacaoFilmeDTO(avaliacaoFilme);
    }
    
    @Transactional
    public void deletar(UUID id){

        AvaliacaoFilme avaliacaoFilme = avaliacaoFilmeRepository.findById(id)
            .orElseThrow(()-> new ResourceNotFoundException("Avaliação não encontrada"));

        avaliacaoFilmeRepository.delete(avaliacaoFilme);    
    }


}
