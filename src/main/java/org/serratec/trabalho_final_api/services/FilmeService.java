package org.serratec.trabalho_final_api.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.transaction.Transactional;
import org.serratec.trabalho_final_api.domain.Categoria;
import org.serratec.trabalho_final_api.domain.Filme;
import org.serratec.trabalho_final_api.dto.request.FilmeRequestDTO;
import org.serratec.trabalho_final_api.dto.response.FilmeResponseDTO;
import org.serratec.trabalho_final_api.dto.response.TmdbDetalhesDTO;
import org.serratec.trabalho_final_api.dto.response.TmdbResponseDTO;
import org.serratec.trabalho_final_api.enumerated.ClassificacaoIndicativa;
import org.serratec.trabalho_final_api.exception.RecursoNaoEncontradoException;
import org.serratec.trabalho_final_api.repository.CategoriaRepository;
import org.serratec.trabalho_final_api.repository.FilmeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FilmeService {

    @Autowired
    private FilmeRepository filmeRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private TmdbService tmdbService;

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
        filme.setTmdbId(dto.getTmdbId());

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
        novoFilme.setTmdbId(dto.getTmdbId());

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

    @Transactional
    public List<FilmeResponseDTO> buscarCatalogoUnificado(String query) {
        List<FilmeResponseDTO> resultadoFinal = new ArrayList<>();

        // 1. Busca no Banco de Dados Local
        List<Filme> filmesLocais = filmeRepository.findByTituloContainingIgnoreCase(query);
        for (Filme filme : filmesLocais) {
            resultadoFinal.add(new FilmeResponseDTO(filme));
        }

        // 2. Busca na API Externa (TMDB)
        TmdbResponseDTO filmesExternos = tmdbService.pesquisarFilmesNoTmdb(query);
        if (filmesExternos != null && filmesExternos.getResultados() != null) {
            for (TmdbResponseDTO.TmdbFilmeItem itemExterno : filmesExternos.getResultados()) {

                // Evita duplicar na tela se o filme do TMDB já estiver cadastrado no seu banco local
                boolean jaExisteLocalmente = filmesLocais.stream()
                        .anyMatch(f -> itemExterno.getId().equals(f.getTmdbId()));

                if (!jaExisteLocalmente) {
                    // CORREÇÃO AQUI: Primeiro geramos o DTO base
                    FilmeResponseDTO dtoExterno = itemExterno.paraFilmeResponseDTO();

                    // CHAMA O ENRIQUECIMENTO: Agora sim ele vai buscar o runtime e a classificação do Brasil!
                    enriquecerComDetalhesDoTmdb(dtoExterno, itemExterno.getId());

                    // ADICIONA NO RESULTADO: Adiciona o objeto já completo
                    resultadoFinal.add(dtoExterno);
                }
            }
        }

        return resultadoFinal;
    }

    private ClassificacaoIndicativa traduzirClassificacao(String certificacao) {
        if (certificacao == null || certificacao.isEmpty() || certificacao.equalsIgnoreCase("L")) {
            return ClassificacaoIndicativa.LIVRE;
        }

        switch (certificacao) {
            case "10": return ClassificacaoIndicativa.DEZ;
            case "12": return ClassificacaoIndicativa.DOZE;
            case "14": return ClassificacaoIndicativa.QUATORZE;
            case "16": return ClassificacaoIndicativa.DEZESSEIS;
            case "18": return ClassificacaoIndicativa.DEZOITO;
            default: return ClassificacaoIndicativa.LIVRE;
        }
    }

    private void enriquecerComDetalhesDoTmdb(FilmeResponseDTO dto, Long tmdbId) {
        TmdbDetalhesDTO detalhes = tmdbService.buscarFilmeExterno(tmdbId);

        if (detalhes != null) {
            System.out.println("Filme: " + dto.getTitulo() + " -> Runtime vindo do TMDB: " + detalhes.getRuntime());

            dto.setDuracao(detalhes.getRuntime());

            String certificacaoBr = "L";
            if (detalhes.getReleaseDates() != null && detalhes.getReleaseDates().getResults() != null) {
                for (TmdbDetalhesDTO.PaisResult pais : detalhes.getReleaseDates().getResults()) {
                    if ("BR".equals(pais.getIsoCodigo())) {
                        if (pais.getReleaseDates() != null) {
                            for (TmdbDetalhesDTO.CertificacaoItem item : pais.getReleaseDates()) {
                                if (item.getCertification() != null && !item.getCertification().isEmpty()) {
                                    certificacaoBr = item.getCertification();
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
            }

            dto.setClassificacaoIndicativa(traduzirClassificacao(certificacaoBr));
        } else {
            System.out.println("Atenção: Detalhes do filme " + dto.getTitulo() + " vieram NULOS do serviço.");
        }
    }


}
