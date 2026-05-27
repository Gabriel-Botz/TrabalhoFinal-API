package org.serratec.trabalho_final_api.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.serratec.trabalho_final_api.domain.Filme;
import org.serratec.trabalho_final_api.domain.ListaFavoritos;
import org.serratec.trabalho_final_api.domain.Series;
import org.serratec.trabalho_final_api.domain.Usuario;
import org.serratec.trabalho_final_api.dto.request.ListaFavoritosRequestDTO;
import org.serratec.trabalho_final_api.dto.response.FilmeResponseDTO;
import org.serratec.trabalho_final_api.dto.response.ListaFavoritosResponseDTO;
import org.serratec.trabalho_final_api.dto.response.SeriesResponseDTO;
import org.serratec.trabalho_final_api.exception.AcessoNegadoException;
import org.serratec.trabalho_final_api.exception.RecursoNaoEncontradoException;
import org.serratec.trabalho_final_api.repository.FilmeRepository;
import org.serratec.trabalho_final_api.repository.ListaFavoritosRepository;
import org.serratec.trabalho_final_api.repository.SeriesRepository;
import org.serratec.trabalho_final_api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class ListaFavoritosService {

    @Autowired
    private ListaFavoritosRepository listaFavoritosRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FilmeRepository filmeRepository;

    @Autowired
    private SeriesRepository seriesRepository;


    // Listas públicas
    public List<ListaFavoritosResponseDTO> listarPublicas() {

        List<ListaFavoritos> listaFavoritos = listaFavoritosRepository.findAll();
        List<ListaFavoritosResponseDTO> listaFavoritosDTO = new ArrayList<>();

        for (ListaFavoritos lista : listaFavoritos) {
            List<FilmeResponseDTO> filmes = new ArrayList<>();
            List<SeriesResponseDTO> series = new ArrayList<>();

            // Adiciona somente listas públicas
            if (!lista.getPrivada()) {

                lista.getFilmes().forEach(filme -> {
                    filmes.add(new FilmeResponseDTO(filme));
                });
                lista.getSeries().forEach(serie -> {
                    series.add(new SeriesResponseDTO(serie));
                });

                listaFavoritosDTO.add(new ListaFavoritosResponseDTO(
                    lista.getId(),
                    lista.getNomeLista(),
                    lista.getPrivada(),
                    lista.getDataCriacao(),
                    lista.getUsuario(),
                    filmes,
                    series
                ));
            }
        }
        
        System.out.println();
        System.out.println("                        _//_");
        System.out.println("                       (o  )>");
        System.out.println("                _      /   /");
        System.out.println("               \\\\ \\\\  /   /");
        System.out.println("                \\\\ \\\\/   /");
        System.out.println("                 \\\\    /");
        System.out.println("                  \\\\__/");
        System.out.println("                  |  |");
        System.out.println("                 _|_ _|_");
        System.out.println("...............OLHA O GALO AÍ....");


        return listaFavoritosDTO;

    }


    // Listas privadas
    // No seu ListaFavoritosService.java, altere este método:
    public List<ListaFavoritosResponseDTO> listarPrivadas(String username) {

        List<ListaFavoritos> listaFavoritos = listaFavoritosRepository.findAll();
        List<ListaFavoritosResponseDTO> listaFavoritosDTO = new ArrayList<>();

        for (ListaFavoritos lista : listaFavoritos) {

            if(username.equals(lista.getUsuario().getUsername())){

                List<FilmeResponseDTO> filmes = new ArrayList<>();
                List<SeriesResponseDTO> series = new ArrayList<>();


                lista.getFilmes().forEach(filme -> {
                    filmes.add(new FilmeResponseDTO(filme));
                });
                lista.getSeries().forEach(serie -> {
                    series.add(new SeriesResponseDTO(serie));
                });

                listaFavoritosDTO.add(new ListaFavoritosResponseDTO(
                        lista.getId(),
                        lista.getNomeLista(),
                        lista.getPrivada(),
                        lista.getDataCriacao(),
                        lista.getUsuario(),
                        filmes,
                        series
                ));
                // }
            }
        }
        return listaFavoritosDTO;
    }


    // Buscar lista por ID
    public ListaFavoritosResponseDTO buscarPorId(UUID id) {

        ListaFavoritos lista = listaFavoritosRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Lista de favoritos não encontrada com ID: " + id));

        List<FilmeResponseDTO> filmes = new ArrayList<>();
        List<SeriesResponseDTO> series = new ArrayList<>();

        lista.getFilmes().forEach(filme -> {
            filmes.add(new FilmeResponseDTO(filme));
        });
        lista.getSeries().forEach(serie -> {
            series.add(new SeriesResponseDTO(serie));
        });

        return new ListaFavoritosResponseDTO(
                lista.getId(),
                lista.getNomeLista(),
                lista.getPrivada(),
                lista.getDataCriacao(),
                lista.getUsuario(),
                filmes,
                series);

    }


    // Busca por nome, mas somente as listas públicas
    public List<ListaFavoritosResponseDTO> buscarPorNomePublicas(String nome) {

        if (nome.isBlank() || nome == null) {
            throw new RecursoNaoEncontradoException("Nenhuma lista contendo \"" + nome + "\"foi encontrada");
        }

        List<ListaFavoritos> listas = listaFavoritosRepository.findByNomeListaContainingIgnoreCase(nome);
        List<ListaFavoritosResponseDTO> listasDTO = new ArrayList<>();
        listas.forEach(lista -> {
            
        List<FilmeResponseDTO> filmes = new ArrayList<>();
        List<SeriesResponseDTO> series = new ArrayList<>();
            
        lista.getFilmes().forEach(filme -> {
            filmes.add(new FilmeResponseDTO(filme));
        });
        lista.getSeries().forEach(serie -> {
            series.add(new SeriesResponseDTO(serie));
        });

            listasDTO.add(new ListaFavoritosResponseDTO(
                    lista.getId(),
                    lista.getNomeLista(),
                    lista.getPrivada(),
                    lista.getDataCriacao(),
                    lista.getUsuario(),
                    filmes,
                    series));

        });

        return listasDTO;
    }

    
    // // Busca por nome, mas somente as listas privadas
    // public List<ListaFavoritosResponseDTO> buscarPorNomePrivadas(String nome) {

    //     if (nome.isBlank() || nome == null) {
    //         throw new RecursoNaoEncontradoException("Nenhuma lista contendo \"" + nome + "\"foi encontrada");
    //     }

    //     List<ListaFavoritos> listas = listaFavoritosRepository.findByNomeListaContainingIgnoreCase(nome);
    //     List<ListaFavoritosResponseDTO> listasDTO = new ArrayList<>();
    //     listas.forEach(lista -> {

    //         listasDTO.add(new ListaFavoritosResponseDTO(
    //                 lista.getId(),
    //                 lista.getNomeLista(),
    //                 lista.getPrivada(),
    //                 lista.getDataCriacao(),
    //                 lista.getUsuario(),
    //                 lista.getFilmes(),
    //                 lista.getSeries()));

    //     });

    //     return listasDTO;
    // }


    // Cria uma nova lista de favoritos
    @Transactional
    public ListaFavoritosResponseDTO criar(String username, ListaFavoritosRequestDTO listaFavoritosRequestDTO) {

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        ListaFavoritos lista = new ListaFavoritos();

        lista.setNomeLista(listaFavoritosRequestDTO.getNomeLista());
        lista.setPrivada(listaFavoritosRequestDTO.getPrivada());
        lista.setDataCriacao(LocalDate.now());
        lista.setUsuario(usuario);

        ListaFavoritos novaLista = listaFavoritosRepository.save(lista);

        return new ListaFavoritosResponseDTO(
                novaLista.getId(),
                novaLista.getNomeLista(),
                novaLista.getPrivada(),
                novaLista.getDataCriacao(),
                novaLista.getUsuario(),
                new ArrayList<>(),
                new ArrayList<>());

    }


    // Atualiza lista de favoritos
    @Transactional
    public ListaFavoritosResponseDTO atualizar(UUID id, ListaFavoritosRequestDTO listaFavoritosRequestDTO,
            String username) {

        ListaFavoritos listaExistente = listaFavoritosRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Lista de favoritos não encontrada com ID: " + id));
                        
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        if (!listaExistente.getUsuario().equals(usuario)) // <- Aqui
            throw new AcessoNegadoException("Você não tem permissão para alterar esta lista.");

        listaExistente.setNomeLista(listaFavoritosRequestDTO.getNomeLista());
        listaExistente.setPrivada(listaFavoritosRequestDTO.getPrivada());

        ListaFavoritos listaAtualizada = listaFavoritosRepository.save(listaExistente);

        List<FilmeResponseDTO> filmes = new ArrayList<>();
        List<SeriesResponseDTO> series = new ArrayList<>();
        
        listaAtualizada.getFilmes().forEach(filme -> {
            filmes.add(new FilmeResponseDTO(filme));
        });
        listaAtualizada.getSeries().forEach(serie -> {
            series.add(new SeriesResponseDTO(serie));
        });

        return new ListaFavoritosResponseDTO(
                listaAtualizada.getId(),
                listaAtualizada.getNomeLista(),
                listaAtualizada.getPrivada(),
                listaAtualizada.getDataCriacao(),
                listaAtualizada.getUsuario(),
                filmes,
                series
            );

    }


    // Apaga um filme
    @Transactional
    public void deletar(String username, UUID id) {

        ListaFavoritos listaExistente = listaFavoritosRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lista não encontrada"));
        
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        if(!listaExistente.getUsuario().equals(usuario))
            throw new AcessoNegadoException("Você não tem permissão para deletar esta lista.");

        listaFavoritosRepository.delete(listaExistente);
    }


    // Adiciona um filme à uma lista de favoritos
    @Transactional
    public ListaFavoritosResponseDTO adicionarFilme(String username, UUID idLista, UUID idFilme) {
        
        // Verifica se o username existe
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
        
        // Busca a lista de favoritos pelo ID
        ListaFavoritos listaFavoritos = listaFavoritosRepository.findById(idLista)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lista de favoritos não encontrada"));
        
        // Verifica se o usuárioi da lista de favoritos é o mesmo do username informado
        if(!listaFavoritos.getUsuario().equals(usuario))
            throw new AcessoNegadoException("Você não tem permissão para alterar esta lista.");

        // Filmes presentes na lista de favoritos atual
        List<Filme> filmes = new ArrayList<>();
        filmes = listaFavoritos.getFilmes();
    
        // Busca o novo filme a ser adicionado pelo ID fornecido
        Filme novoFilme = filmeRepository.findById(idFilme)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nenhum filme encontrado com o ID fornecido"));

        // Verifica se o filme já está presente na lista de favoritos
        if (!filmes.contains(novoFilme)) {
            // Adiciona o novo filme à lista de filmes
            filmes.add(novoFilme);
        }

        // Transforma as listas filme e séries em DTO
        List<FilmeResponseDTO> filmesDTO = new ArrayList<>();
        List<SeriesResponseDTO> seriesDTO = new ArrayList<>();

        filmes.forEach(filme -> {
            filmesDTO.add(new FilmeResponseDTO(filme));
        });
        listaFavoritos.getSeries().forEach(serie -> {
            seriesDTO.add(new SeriesResponseDTO(serie));
        });

        // Salva a lista de favoritos atualizada no banco de dados
        listaFavoritos = listaFavoritosRepository.save(listaFavoritos);

        // Transforma a lista de favoritos atualizada em DTO para retornar na resposta
        ListaFavoritosResponseDTO listaFavoritosDTO = new ListaFavoritosResponseDTO(
            listaFavoritos.getId(),
            listaFavoritos.getNomeLista(),
            listaFavoritos.getPrivada(),
            listaFavoritos.getDataCriacao(),
            listaFavoritos.getUsuario(),
            filmesDTO,
            seriesDTO
        );
        
        // Retorna a lista de favoritos atualizada com o novo filme
        return listaFavoritosDTO;

    }


    // Adiciona uma série à uma lista de favoritos
    @Transactional
    public ListaFavoritosResponseDTO adicionarSerie(String username, UUID idLista, UUID idSerie) {
        
        // Verifica se o username existe
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
        
        // Busca a lista de favoritos pelo ID
        ListaFavoritos listaFavoritos = listaFavoritosRepository.findById(idLista)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lista de favoritos não encontrada"));
        
        // Verifica se o usuárioi da lista de favoritos é o mesmo do username informado
        if(!listaFavoritos.getUsuario().equals(usuario))
            throw new AcessoNegadoException("Você não tem permissão para alterar esta lista.");

        // Series presentes na lista de favoritos atual
        List<Series> series = new ArrayList<>();
        series = listaFavoritos.getSeries();
    
        // Busca a nova série a ser adicionado pelo ID fornecido
        Series novaSerie = seriesRepository.findById(idSerie)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nenhuma série encontrado com o ID fornecido"));

        // Verifica se a série já está presente na lista de favoritos
        if (!series.contains(novaSerie)) {

            // Adiciona a nova série à lista de filmes
            series.add(novaSerie);
        }

        // Salva a lista de favoritos atualizada no banco de dados
        listaFavoritos = listaFavoritosRepository.save(listaFavoritos);

        // Transforma as listas filme e séries em DTO
        List<FilmeResponseDTO> filmesDTO = new ArrayList<>();
        List<SeriesResponseDTO> seriesDTO = new ArrayList<>();

        series.forEach(serie -> {
            seriesDTO.add(new SeriesResponseDTO(serie));
        });
        listaFavoritos.getFilmes().forEach(filme -> {
            filmesDTO.add(new FilmeResponseDTO(filme));
        });

        // Retorna a lista de favoritos atualizada com o novo filme
        return new ListaFavoritosResponseDTO(
            listaFavoritos.getId(),
            listaFavoritos.getNomeLista(),
            listaFavoritos.getPrivada(),
            listaFavoritos.getDataCriacao(),
            listaFavoritos.getUsuario(),
            filmesDTO,
            seriesDTO
        );
        

    }


    // Remove um filme de uma lista de favoritos
    @Transactional
    public ListaFavoritosResponseDTO removerFilme(String username, UUID idLista, UUID idFilme) {
        // Verifica se o username existe
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
        
        // Busca a lista de favoritos pelo ID
        ListaFavoritos listaFavoritos = listaFavoritosRepository.findById(idLista)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lista de favoritos não encontrada"));
        
        // Verifica se o usuárioi da lista de favoritos é o mesmo do username informado
        if(!listaFavoritos.getUsuario().equals(usuario))
            throw new AcessoNegadoException("Você não tem permissão para alterar esta lista.");

        // Filmes presentes na lista de favoritos atual
        List<Filme> filmes = new ArrayList<>();
        filmes = listaFavoritos.getFilmes();
    
        // Busca o filme a ser removido pelo ID fornecido
        Filme filmeRemover = filmeRepository.findById(idFilme)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nenhum filme encontrado com o ID fornecido"));

        // Remove o filme da lista de filmes
        filmes.remove(filmeRemover);

        // Salva a lista de favoritos atualizada no banco de dados
        listaFavoritosRepository.save(listaFavoritos);

        // Transforma as listas filme e séries em DTO
        List<FilmeResponseDTO> filmesDTO = new ArrayList<>();
        List<SeriesResponseDTO> seriesDTO = new ArrayList<>();

        filmes.forEach(filme -> {
            filmesDTO.add(new FilmeResponseDTO(filme));
        });
        listaFavoritos.getSeries().forEach(serie -> {
            seriesDTO.add(new SeriesResponseDTO(serie));
        });

        // Retorna a lista de favoritos atualizada sem o filme removido
        return new ListaFavoritosResponseDTO(
            listaFavoritos.getId(),
            listaFavoritos.getNomeLista(),
            listaFavoritos.getPrivada(),
            listaFavoritos.getDataCriacao(),
            listaFavoritos.getUsuario(),
            filmesDTO,
            seriesDTO
        );
    }


     // Remove uma série de uma lista de favoritos
     @Transactional
     public ListaFavoritosResponseDTO removerSerie(String username, UUID idLista, UUID idSerie) {
        // Verifica se o username existe
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
        
        // Busca a lista de favoritos pelo ID
        ListaFavoritos listaFavoritos = listaFavoritosRepository.findById(idLista)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lista de favoritos não encontrada"));
        
        // Verifica se o usuárioi da lista de favoritos é o mesmo do username informado
        if(!listaFavoritos.getUsuario().equals(usuario))
            throw new AcessoNegadoException("Você não tem permissão para alterar esta lista.");

        // Series presentes na lista de favoritos atual
        List<Series> series = new ArrayList<>();
        series = listaFavoritos.getSeries();
    
        // Busca a série a ser removida pelo ID fornecido
        Series serieRemover = seriesRepository.findById(idSerie)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nenhuma série encontrado com o ID fornecido"));

        // Remove a série da lista de séries
        series.remove(serieRemover);

        // Salva a lista de favoritos atualizada no banco de dados
        listaFavoritosRepository.save(listaFavoritos);

        // Transforma as listas filme e séries em DTO
        List<FilmeResponseDTO> filmesDTO = new ArrayList<>();
        List<SeriesResponseDTO> seriesDTO = new ArrayList<>();

        series.forEach(serie -> {
            seriesDTO.add(new SeriesResponseDTO(serie));
        });
        listaFavoritos.getFilmes().forEach(filme -> {
            filmesDTO.add(new FilmeResponseDTO(filme));
        });

        // Retorna a lista de favoritos atualizada sem a série removida
        return new ListaFavoritosResponseDTO(
            listaFavoritos.getId(),
            listaFavoritos.getNomeLista(),
            listaFavoritos.getPrivada(),
            listaFavoritos.getDataCriacao(),
            listaFavoritos.getUsuario(),
            filmesDTO,
            seriesDTO
        );
    }

}
