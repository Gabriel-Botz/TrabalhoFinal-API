package org.serratec.TrabalhoFinal_API.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.transaction.Transactional;
import org.serratec.TrabalhoFinal_API.domain.Categoria;
import org.serratec.TrabalhoFinal_API.domain.Filme;
import org.serratec.TrabalhoFinal_API.dto.request.FilmeRequestDTO;
import org.serratec.TrabalhoFinal_API.dto.response.FilmeResponseDTO;
import org.serratec.TrabalhoFinal_API.exception.RecursoNaoEncontradoException;
import org.serratec.TrabalhoFinal_API.repository.CategoriaRepository;
import org.serratec.TrabalhoFinal_API.repository.FilmeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FilmeService {

    @Autowired
    private FilmeRepository filmeRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<FilmeResponseDTO> listarFilmes() {
        List<Filme> listaFilmes = filmeRepository.findAll();
        List<FilmeResponseDTO> listaResponse = new ArrayList<>();

        for (Filme filme : listaFilmes) {
            listaResponse.add(new FilmeResponseDTO(filme));
        }

        return listaResponse;
    }

    @Transactional
    public FilmeResponseDTO atualizarFilme(UUID id, FilmeRequestDTO dto) {
        Filme filme = filmeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Filme não encontrado"));

        filme.setTitulo(dto.getTitulo());
        filme.setDescricao(dto.getDescricao());
        filme.setDuracao(dto.getDuracao());
        filme.setDataLancamento(dto.getDataLancamento());
        filme.setClassificacaoIndicativa(dto.getClassificacaoIndicativa());

        return new FilmeResponseDTO(filmeRepository.save(filme));
    }

    @Transactional
    public FilmeResponseDTO buscarFilmePorId(UUID id) {
        Filme filme = filmeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Filme não encontrado"));

        return new FilmeResponseDTO(filme);
    }

    @Transactional
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

    @Transactional
    public void deletarFilme(UUID id) {

        if (!filmeRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Filme não encontrado");
        }
        filmeRepository.deleteById(id);
    }

    @Transactional
    public FilmeResponseDTO vincularCategoria(UUID filmeId, Long categoriaId) {
        Filme filme = filmeRepository.findById(filmeId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Filme não encontrado"));

        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada"));

        filme.getCategorias().add(categoria);
        return new FilmeResponseDTO(filmeRepository.save(filme));
    }

    @Transactional
    public List<FilmeResponseDTO> buscarFilmesPorCategoria(Long categoriaId) {
        return filmeRepository.findByCategorias_Id(categoriaId)
                .stream()
                .map(FilmeResponseDTO::new)
                .toList();
    }

}
