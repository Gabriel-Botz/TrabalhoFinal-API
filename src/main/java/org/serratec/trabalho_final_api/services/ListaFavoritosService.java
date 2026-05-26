package org.serratec.trabalho_final_api.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.serratec.trabalho_final_api.domain.ListaFavoritos;
import org.serratec.trabalho_final_api.domain.Usuario;
import org.serratec.trabalho_final_api.dto.request.ListaFavoritosRequestDTO;
import org.serratec.trabalho_final_api.dto.response.FilmeResponseDTO;
import org.serratec.trabalho_final_api.dto.response.ListaFavoritosResponseDTO;
import org.serratec.trabalho_final_api.dto.response.SeriesResponseDTO;
import org.serratec.trabalho_final_api.exception.AcessoNegadoException;
import org.serratec.trabalho_final_api.exception.RecursoNaoEncontradoException;
import org.serratec.trabalho_final_api.repository.ListaFavoritosRepository;
import org.serratec.trabalho_final_api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class ListaFavoritosService {

    @Autowired
    private ListaFavoritosRepository listaFavoritosRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PermissaoService permissaoService;

    // Listars públicas
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

        return listaFavoritosDTO;

    }


    // Listas privadas
    public List<ListaFavoritosResponseDTO> listarPrivadas(String username) {

        List<ListaFavoritos> listaFavoritos = listaFavoritosRepository.findAll();
        List<ListaFavoritosResponseDTO> listaFavoritosDTO = new ArrayList<>();
        
        for (ListaFavoritos lista : listaFavoritos) {

            if(username.equals(lista.getUsuario().getUsername())){

                List<FilmeResponseDTO> filmes = new ArrayList<>();
                List<SeriesResponseDTO> series = new ArrayList<>();
    
                if (lista.getPrivada()) {
    
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

        }
        return listaFavoritosDTO;
    }


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


    // Atualizar lista de favoritos
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
}
