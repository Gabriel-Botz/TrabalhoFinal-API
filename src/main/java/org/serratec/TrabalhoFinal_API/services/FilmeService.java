package org.serratec.TrabalhoFinal_API.services;

import org.hibernate.validator.constraints.UUID;
import org.serratec.TrabalhoFinal_API.domain.Filme;
import org.serratec.TrabalhoFinal_API.dto.request.FilmeRequestDTO;
import org.serratec.TrabalhoFinal_API.dto.response.FilmeResponseDTO;
import org.serratec.TrabalhoFinal_API.repository.FilmeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FilmeService {

    @Autowired
    private FilmeRepository filmeRepository;

    public List<FilmeResponseDTO> listarFilmes() {
        List<Filme> listaFilmes = filmeRepository.findAll();
        List<FilmeResponseDTO> listaResponse = new ArrayList<>();

        for (Filme filme : listaFilmes) {
            listaResponse.add(new FilmeResponseDTO(filme));
        }

        return listaResponse;
    }

    public FilmeResponseDTO atualizarFilme(UUID id, FilmeRequestDTO dto) {
        Filme filme = filmeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Filme não encontrado"));

        filme.setTitulo(dto.getTitulo());
        filme.setDescricao(dto.getDescricao());
        filme.setDuracao(dto.getDuracao());
        filme.setDataLancamento(dto.getDataLancamento());
        filme.setClassificacaoIndicativa(dto.getClassificacaoIndicativa());

        return new FilmeResponseDTO(filmeRepository.save(filme));
    }

    public FilmeResponseDTO criarFilme(FilmeRequestDTO dto){
        var novoFilme = new Filme();
        novoFilme.setTitulo(dto.getTitulo());
        novoFilme.setDescricao(dto.getDescricao());
        novoFilme.setDuracao(dto.getDuracao());
        novoFilme.setDataLancamento(dto.getDataLancamento());
        novoFilme.setClassificacaoIndicativa(dto.getClassificacaoIndicativa());

        var filmeSalvo = filmeRepository.save(novoFilme);
        return new FilmeResponseDTO(filmeSalvo);
    }

}
