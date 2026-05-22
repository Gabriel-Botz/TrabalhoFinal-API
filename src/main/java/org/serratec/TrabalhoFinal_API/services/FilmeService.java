package org.serratec.TrabalhoFinal_API.services;

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

    public List<FilmeResponseDTO> listarFilmes(){
        List<Filme> listaFilmes = filmeRepository.findAll();
        List<FilmeResponseDTO> listaFilmeResponseDTO = new ArrayList<>();

        for (Filme filme : listaFilmes) {
            FilmeResponseDTO filmeResponseDTO = new FilmeResponseDTO(
                    filme.getId(),
                    filme.getTitulo(),
                    filme.getDescricao(),
                    filme.getDuracao(),
                    filme.getDataLancamento(),
                    filme.getNotaMedia(),
                    filme.getClassificacaoIndicativa()
            );
        }

        return listaFilmeResponseDTO;
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
